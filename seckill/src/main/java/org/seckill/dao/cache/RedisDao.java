package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDao {
    private JedisPool jedisPool;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private RuntimeSchema<Stock> schema = RuntimeSchema.createFrom(Stock.class);

    public RedisDao(String ip, int port) {
        jedisPool = new JedisPool(ip, port);
    }

    public Stock getStock(long seckillId) {
        //redis操作
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String key = "seckill:" + seckillId;
            //redis没有实现序列化
            //采用自定义序列化protostuff
            byte[] bytes = jedis.get(key.getBytes());
            if (bytes != null) {
                Stock stock = schema.newMessage();//产生一个空对象
                ProtostuffIOUtil.mergeFrom(bytes, stock, schema);//数据存入空对象
                return stock;
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public String putStock(Stock stock) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String key = "seckill:" + stock.getSeckillId();

            byte[] bytes = ProtostuffIOUtil.toByteArray(stock, schema,
                    LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));//对象转成字节数组
            int timeout = 60 * 60;//一小时过期
            String result = jedis.setex(key.getBytes(), timeout, bytes);//存入成功：OK 失败：错误信息
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }
}
