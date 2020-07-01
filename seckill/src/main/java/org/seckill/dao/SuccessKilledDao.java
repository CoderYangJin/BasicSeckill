package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;

public interface SuccessKilledDao {
    /*插入购买明细，通过联合主键过滤重复*/
    int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone")long userPhone,@Param("state")int state);

    SuccessKilled queryByIdWithStock(@Param("seckillId") long seckillId, @Param("userPhone")long userPhone);
}
