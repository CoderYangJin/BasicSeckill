package org.seckill.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExcution;
import org.seckill.entity.Stock;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class SeckillServiceImplTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService;

    @Test
    public void testGetSeckillList() {
        List<Stock> list = seckillService.getSeckillList();
        logger.info("list:{}", list);
    }

    @Test
    public void testGetById() {
        Stock stock = seckillService.getById(1000);
        logger.info("stock:{}", stock);
    }

    @Test
    public void testExportSeckillUrl() {
        Exposer exposer = seckillService.exportSeckillUrl(1001);
        logger.info("exposer:{}", exposer);
    }

    @Test
    public void testExcuteSeckill() {
        String md5 = "bae317fc7b1b24b302c0c25cebb39657";
        try {
            SeckillExcution seckillExcution = seckillService.excuteSeckill(1001, 10010, md5);
            logger.info("seckillExcution:{}", seckillExcution);
        } catch (SeckillCloseException e1) {
            logger.error(e1.getMessage(), e1);
        } catch (RepeatKillException e2) {
            logger.error(e2.getMessage(), e2);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    @Test
    public void testSeckillLogic() {
        //测试exportSeckillUrl和excuteSeckill
        int id = 1000;
        long phone = 100010L;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if (exposer.isExposed()) {
            logger.info("exposer:{}", exposer);
            String md5 = exposer.getMd5();
            try {
                SeckillExcution seckillExcution = seckillService.excuteSeckill(id, phone, md5);
                logger.info("seckillExcution:{}", seckillExcution);
            } catch (SeckillCloseException e1) {//catch可重复测试
                logger.error(e1.getMessage(), e1);
            } catch (RepeatKillException e2) {
                logger.error(e2.getMessage(), e2);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            logger.warn("exposer:{}", exposer);
        }

    }

    @Test
    public void testProcedureSeckillLogic() {
        int id = 1002;
        long phone = 100010L;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if (exposer.isExposed()) {
            logger.info("exposer:{}", exposer);
            String md5 = exposer.getMd5();
            SeckillExcution seckillExcution = seckillService.excuteSeckillProcedure(id, phone, md5);
            logger.info("StateInfo:{}", seckillExcution.getStateInfo());
        } else {
            logger.warn("exposer:{}", exposer);
        }
    }

}