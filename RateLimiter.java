package in.sp.main;

import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter {

    private static final int MAX_ATTEMPTS = 5;       // max allowed attempts
    private static final long WINDOW_MS = 5 * 60 * 1000; // 5 minutes window
    private static final long BLOCK_MS = 2 * 60 * 1000;  // 2 minutes block


    // Track attempts for every username
    private static ConcurrentHashMap<String, AttemptInfo> attemptsMap = new ConcurrentHashMap<>();

    private static class AttemptInfo {
        int count;
        long firstAttemptTime;
        long blockedUntil;
    }

    // Check if user is blocked or not
    public static boolean isBlocked(String username) {
        AttemptInfo info = attemptsMap.get(username);
        if (info == null) return false;

        long now = System.currentTimeMillis();

        // if block time still running 
        if (info.blockedUntil > now) {
            return true;
        }

        // Reset it if block time is end
        if (info.blockedUntil != 0 && info.blockedUntil <= now) {
            attemptsMap.remove(username);
        }

        return false;
    }

    // Call on a Failed login
    public static void recordFailedAttempt(String username) {
        long now = System.currentTimeMillis();

        AttemptInfo info = attemptsMap.get(username);

        if (info == null) {
            info = new AttemptInfo();
            info.count = 1;
            info.firstAttemptTime = now;
            attemptsMap.put(username, info);
            return;
        }

        // Reset if a window is expire in a 5 minute 
        if (now - info.firstAttemptTime > WINDOW_MS) {
            info.count = 1;
            info.firstAttemptTime = now;
            info.blockedUntil = 0;
            return;
        }

        info.count++;

        // If attempt is greater than 5 so block 
        if (info.count > MAX_ATTEMPTS) {
            info.blockedUntil = now + BLOCK_MS;
        }
    }

    // If Successful login so reset
    public static void resetAttempts(String username) {
        attemptsMap.remove(username);
    }

    // How much time is remaining in a block end (seconds) 
    public static long getRemainingBlockTime(String username) {
        AttemptInfo info = attemptsMap.get(username);
        if (info == null) return 0;

        long remaining = info.blockedUntil - System.currentTimeMillis();
        return remaining > 0 ? remaining / 1000 : 0;
    }
}