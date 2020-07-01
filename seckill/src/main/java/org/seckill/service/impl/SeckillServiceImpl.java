package org.seckill.service.impl;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.StockDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExcution;
import org.seckill.entity.Stock;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeckillServiceImpl implements SeckillService {
    @Autowired
    private StockDao stockDao;
    @Autowired
    private SuccessKilledDao successKilledDao;
    @Autowired
    private RedisDao redisDao;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String salt = "cnqJQ_)!)D*E@kp;cjO_*^qPOU(DW";

    @Override
    public List<Stock> getSeckillList() {
        return stockDao.queryAll(0, 4);
    }

    @Override
    public Stock getById(long seckillId) {
        return stockDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        //redis获取数据
        Stock stock = redisDao.getStock(seckillId);
        if (stock == null) {
            stock = stockDao.queryById(seckillId);
            if (stock == null) {
                //没有该商品
                return new Exposer(false, seckillId);
            } else {
                redisDao.putStock(stock);
            }
        }
        long startTime = stock.getStartTime().getTime();
        long endTime = stock.getEndTime().getTime();
        long now = new Date().getTime();
        //未在秒杀时间
        if (now < startTime || now > endTime) {
            return new Exposer(false, seckillId, now, startTime, endTime);
        }
        String md5 = getMD5(seckillId);
        //秒杀开始了 返回md5 才可以拼接url
        return new Exposer(true, md5, seckillId);
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + salt;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }

    @Override
    @Transactional
    public SeckillExcution excuteSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            logger.error("seckill data rewrite");
            throw new SeckillException("seckill data rewrite");
        }

        //抛出异常会回滚
        try {
            //秒杀逻辑
            //1记录购买行为
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone, 0);
            if (insertCount <= 0) {
                //重复秒杀
                throw new RepeatKillException("seckill repeated");
            } else {
                //2减库存(21比12持有行级锁的时间长)
                int updateCount = stockDao.reduceNumber(seckillId, new Date());
                if (updateCount <= 0) {
                    //没有更新数据 说明秒杀结束（时间不在范围内||无库存）
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    SuccessKilled successKilled = successKilledDao.queryByIdWithStock(seckillId, userPhone);
                    return new SeckillExcution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillCloseException e1) {
            logger.error(e1.getMessage(), e1);
            throw e1;
        } catch (RepeatKillException e2) {
            logger.error(e2.getMessage(), e2);
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SeckillException("seckill innner error:" + e.getMessage());
        }

    }

    //通过存储过程执行秒杀
    @Override
    public SeckillExcution excuteSeckillProcedure(long seckillId, long userPhone, String md5) {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            logger.error("seckill data rewrite");
            throw new SeckillException("seckill data rewrite");
        }
        Date time = new Date();
        Map<String, Object> map = new HashMap<>();
        map.put("seckillId", seckillId);
        map.put("phone", userPhone);
        map.put("killTime", time);
        map.put("result", null);
        try {
            stockDao.killByProcedure(map);
            //从map获取执行结果result
            Integer result = MapUtils.getInteger(map, "result");
            if (result == 1) {
                SuccessKilled successKilled = successKilledDao.queryByIdWithStock(seckillId, userPhone);
                return new SeckillExcution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
            } else {
                return new SeckillExcution(seckillId, SeckillStateEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new SeckillExcution(seckillId, SeckillStateEnum.INNER_ERROR);
        }
    }
}
