package org.seckill.dao.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.StockDao;
import org.seckill.entity.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest {
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private StockDao stockDao;

    private long id = 1001;

    @Test
    public void testRedis() {
        Stock stock = redisDao.getStock(id);
        if (stock == null) {
            stock = stockDao.queryById(id);
            if (stock != null) {
                String result = redisDao.putStock(stock);
                System.out.println(result);
                stock = redisDao.getStock(id);
                System.out.println(stock);
            }
        }
    }


}