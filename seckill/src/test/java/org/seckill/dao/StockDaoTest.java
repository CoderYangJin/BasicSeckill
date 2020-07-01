package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class StockDaoTest {
    @Autowired
    private StockDao stockDao;

    @Test
    public void testQueryById() throws Exception {
        long id=1000;
        Stock stock = stockDao.queryById(id);
        System.out.println(stock.getName());
        System.out.println(stock);
    }

    @Test
    public void testQueryAll() throws Exception {
        List<Stock> stockList = stockDao.queryAll(0, 100);
        for (Stock stock : stockList) {
            System.out.println(stock);
        }
    }

    @Test
    public void testReduceNumber() throws Exception {
        long id=1000;
        Stock stock = stockDao.queryById(id);
        System.out.println(stock.getNumber());
        int i = stockDao.reduceNumber(id, new Date());
        System.out.println(i);
         stock = stockDao.queryById(id);
        System.out.println(stock.getNumber());
    }


}