package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {
    @Autowired
    private SuccessKilledDao successKilledDao;

    @Test
    public void testInsertSuccessKilled() throws Exception {
        long id = 1001;
        long phone = 100861;
        int state=0;
        int i = successKilledDao.insertSuccessKilled(id, phone,state);
        System.out.println(i);
    }

    @Test
    public void testQueryByIdWithStock() throws Exception {
        long id = 1001;
        long phone = 100861;
        SuccessKilled successKilled = successKilledDao.queryByIdWithStock(id, phone);
        System.out.println(successKilled);
        System.out.println(successKilled.getStock());
    }
}