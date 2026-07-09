
package in.sp.main;
import redis.clients.jedis.Jedis;
import java.util.UUID;

public class RedisSessionManager {

    private static final int SESSION_TIMEOUT_SECONDS = 1800; // 30 min

    // login successful generated token save into redis
    public static String createSession(String userId, String role) {
        String token = UUID.randomUUID().toString();
        try (Jedis jedis = RedisUtil.getConnection()) {
            String key = "session:" + token;
            jedis.hset(key, "userId", userId);
            jedis.hset(key, "role", role);
            jedis.expire(key, SESSION_TIMEOUT_SECONDS); // auto expire
        }
        return token;
    }

    // Filter check is token valid or not
    public static boolean isValidSession(String token) {
        if (token == null) return false;
        try (Jedis jedis = RedisUtil.getConnection()) {
            return jedis.exists("session:" + token);
        }
    }

    public static String getUserId(String token) {
        try (Jedis jedis = RedisUtil.getConnection()) {
            return jedis.hget("session:" + token, "userId");
        }
    }

    public static String getRole(String token) {
        try (Jedis jedis = RedisUtil.getConnection()) {
            return jedis.hget("session:" + token, "role");
        }
    }

    // // Refresh the expiration time on every request (sliding expiration, similar to a real session)
    public static void refreshSession(String token) {
        try (Jedis jedis = RedisUtil.getConnection()) {
            jedis.expire("session:" + token, SESSION_TIMEOUT_SECONDS);
        }
    }

    public static void destroySession(String token) {
        try (Jedis jedis = RedisUtil.getConnection()) {
            jedis.del("session:" + token);
        }
    }
}