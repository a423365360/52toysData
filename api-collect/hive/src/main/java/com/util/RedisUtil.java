package com.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.InputStream;
import java.util.Properties;

/**
 * Author: DaSH
 * Desc: 通过连接池获取Jedis的工具类
 */
public class RedisUtil {

    public static JedisPool jedisPool = null;

    public static Jedis getJedis(String localFlag) throws Exception {

        if (jedisPool == null) {
            Properties redisProperty = new Properties();
            InputStream redisResourceSream = RedisUtil.class.getClassLoader().getResourceAsStream("redis.properties");
            redisProperty.load(redisResourceSream);

            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxTotal(10); //最大可用连接数
            jedisPoolConfig.setBlockWhenExhausted(true); //连接耗尽是否等待
            jedisPoolConfig.setMaxWaitMillis(2000); //等待时间
            jedisPoolConfig.setMaxIdle(5); //最大闲置连接数
            jedisPoolConfig.setMinIdle(5); //最小闲置连接数
            jedisPoolConfig.setTestOnBorrow(true); //取连接的时候进行一下测试 ping pong

            String redisHost = "";
            if ("0".equals(localFlag)) {
                redisHost = redisProperty.getProperty("redis_host_test");
            } else if ("1".equals(localFlag)) {
                redisHost = redisProperty.getProperty("redis_host");
            }
            String redisPassword = redisProperty.getProperty("redis_password");

            jedisPool = new JedisPool(jedisPoolConfig, redisHost, 6379, 1000, redisPassword);
            System.out.println("开辟连接池");
            return jedisPool.getResource();

        } else {
            System.out.println(" 连接池:" + jedisPool.getNumActive());
            return jedisPool.getResource();
        }
    }
}
