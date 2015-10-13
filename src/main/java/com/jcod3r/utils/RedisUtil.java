package com.jcod3r.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public final class RedisUtil {
    private static String ADDR = "127.0.0.1";

    private static int PORT = 6379;

    private static int MAX_IDLE = 200;

    private static int MAX_WAIT = 10000;

    private static int TIMEOUT = 10000;

    private static String PWD = "######";

    private static boolean TEST_ON_BORROW = true;

    private static JedisPool jedisPool = null;

    public static synchronized Jedis getJedis() {
        try {
            if (RedisUtil.jedisPool != null) {
                return RedisUtil.jedisPool.getResource();
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void returnResource(Jedis jedis) {
        if (jedis != null) {
            RedisUtil.jedisPool.returnResource(jedis);
        }
    }

    static {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxIdle(RedisUtil.MAX_IDLE);
            config.setMaxWaitMillis(RedisUtil.MAX_WAIT);
            config.setTestOnBorrow(RedisUtil.TEST_ON_BORROW);
            RedisUtil.jedisPool = new JedisPool(config, RedisUtil.ADDR,
                RedisUtil.PORT, RedisUtil.TIMEOUT, RedisUtil.PWD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}