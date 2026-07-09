package in.sp.main;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtil {

    private static JedisPool pool;

    // Static block - after server start one time run
    static {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(50);      // max connections in pool
        poolConfig.setMaxIdle(10);
        poolConfig.setMinIdle(2);
        poolConfig.setTestOnBorrow(true);

        // If server using any remote server so change its port number
        pool = new JedisPool(poolConfig, "localhost", 6379);
    }

    // Always use this method to get a connection, and return it to the pool after use
    public static Jedis getConnection() {
        return pool.getResource();
    }

    public static void closePool() {
        if (pool != null) {
            pool.close();
        }
    }
}