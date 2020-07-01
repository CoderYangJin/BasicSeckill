package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Stock;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface StockDao {
    int reduceNumber(@Param("seckillId")long seckillId, @Param("killTime")Date killTime);

    Stock queryById(long seckillId);

    List<Stock> queryAll(@Param("offset") int offset, @Param("limit") int limit);

    //通过存储过程执行秒杀
    void killByProcedure(Map<String,Object> paramMap);

}
