package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExcution;
import org.seckill.entity.Stock;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

import java.util.List;

public interface SeckillService {
    List<Stock> getSeckillList();

    Stock getById(long seckillId);
    //在秒杀开启时输出秒杀接口的地址,否则输出系统时间跟秒杀地址
    Exposer exportSeckillUrl(long seckillId);
    //执行秒杀
    SeckillExcution excuteSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException;
    //通过存储过程执行秒杀
    SeckillExcution excuteSeckillProcedure(long seckillId, long userPhone, String md5) ;


}
