package in.sp.main;
 
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.servlet.http.Cookie;
import org.mindrot.jbcrypt.BCrypt;
 
@WebServlet("/mylogin")
@MultipartConfig
public class Login extends HttpServlet {
 
    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 2;
 
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("index.html").forward(req, resp);
    }
 
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
 
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
 
        // Use Data form
        String myUsername = getFieldValue(req.getPart("myUsername"));
        String myPassword = getFieldValue(req.getPart("myPassword"));
 
        // Rate limiter check (in-memory)
        if (RateLimiter.isBlocked(myUsername)) {
            long remainingSeconds = RateLimiter.getRemainingBlockTime(myUsername);
            out.print("<h2 style='color:red;'>Too many failed attempts!</h2>");
            out.print("<p style='color:gray;'>Please try again after " + remainingSeconds + " seconds.</p>");
            return;
        }
 
        System.out.println("Login attempt - Username: " + myUsername);
 
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/login_demo", "root", "ses4321");
 
            String sql = "select * from register where register_name = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, myUsername);
 
            ResultSet rs = ps.executeQuery();
 
            if (rs.next()) {
 
                int userId = rs.getInt("register_id");
                Timestamp lockedUntil = rs.getTimestamp("locked_until");
                int failedAttempts = rs.getInt("failed_attempts");
 
                // Account logout check
                if (lockedUntil != null && lockedUntil.after(new Timestamp(System.currentTimeMillis()))) {
                    long remainingMillis = lockedUntil.getTime() - System.currentTimeMillis();
                    long remainingMinutes = (remainingMillis / 1000) / 60 + 1;
 
                    out.print("<h2 style='color:red;'>Account Locked!</h2>");
                    out.print("<p style='color:gray;'>Too many failed attempts. Try again after " + remainingMinutes + " minute(s), or contact admin.</p>");
 
                    rs.close();
                    ps.close();
                    con.close();
                    return;
                }
 
                String storedHashedPassword = rs.getString("register_password");
 
                if (BCrypt.checkpw(myPassword, storedHashedPassword)) {
 
                    // Login success reset failed attempts in DB
                    String resetSql = "UPDATE register SET failed_attempts = 0, locked_until = NULL WHERE register_id = ?";
                    PreparedStatement resetPs = con.prepareStatement(resetSql);
                    resetPs.setInt(1, userId);
                    resetPs.executeUpdate();
                    resetPs.close();
 
                    RateLimiter.resetAttempts(myUsername);
 
                    // Role determine karo pehle (Redis mein store karna hai)
                    String role = myUsername.equals("admin") ? "admin" : "user";
 
                    // ===== REDIS SESSION (HttpSession ki jagah) =====
                    String token = RedisSessionManager.createSession(String.valueOf(userId), role);
 
                    Cookie sessionCookie = new Cookie("SESSION_TOKEN", token);
                    sessionCookie.setHttpOnly(true);
                    sessionCookie.setPath("/");
                    sessionCookie.setMaxAge(1800); // 30 min - Redis TTL ke sath match (RedisSessionManager mein bhi 1800 hai)
                    // sessionCookie.setSecure(true); // production HTTPS pe zaroor on karna
                    resp.addCookie(sessionCookie);
                    // ===== REDIS SESSION END =====
 
                    rs.close();
                    ps.close();
                    con.close();
 
                    if (myUsername.equals("admin")) {
                        resp.sendRedirect("adminDashboard");
                    } else {
                        resp.sendRedirect("welcome");
                    }
                    return;
 
                } else {
                    // Wrong password increase failed attempts
                    RateLimiter.recordFailedAttempt(myUsername);
 
                    failedAttempts++;
 
                    if (failedAttempts >= MAX_ATTEMPTS) {
 
                        // lock account
                        Timestamp lockTime = new Timestamp(System.currentTimeMillis() + (1 * 60 * 1000));
 
                        String lockSql = "UPDATE register SET failed_attempts = ?, locked_until = ? WHERE register_id = ?";
                        PreparedStatement lockPs = con.prepareStatement(lockSql);
                        lockPs.setInt(1, failedAttempts);
                        lockPs.setTimestamp(2, lockTime);
                        lockPs.setInt(3, userId);
                        lockPs.executeUpdate();
                        lockPs.close();
 
                        out.print("<h2 style='color:red;'>Account Locked!</h2>");
                        out.print("<p style='color:gray;'>Too many failed attempts. Account locked for " + LOCK_MINUTES + " minutes.</p>");
 
                    } else {
 
                        // Just updates count attempt
                        String updateSql = "UPDATE register SET failed_attempts = ? WHERE register_id = ?";
                        PreparedStatement updatePs = con.prepareStatement(updateSql);
                        updatePs.setInt(1, failedAttempts);
                        updatePs.setInt(2, userId);
                        updatePs.executeUpdate();
                        updatePs.close();
 
                        int remaining = MAX_ATTEMPTS - failedAttempts;
                        out.print("<h2 style='color:red;'>Login Failed!</h2>");
                        out.print("<p style='color:gray;'>Invalid username or password. " + remaining + " attempt(s) remaining.</p>");
                        out.print("<a href='index.html'>Go Back to Login Page</a>");
                    }
                }
            } else {
                RateLimiter.recordFailedAttempt(myUsername);
                out.print("<h2 style='color:red;'>Login Failed!</h2>");
                out.print("<p style='color:gray;'>Invalid username or password.</p>");
                out.print("<a href='index.html'>Go Back to Login Page</a>");
            }
 
            rs.close();
            ps.close();
            con.close();
 
        } catch (Exception e) {
            out.print("<h2 style='color:red;'>Error occurred!</h2>");
            out.print("<p>" + e.getMessage() + "</p>");
            e.printStackTrace();
        }
    }
 
    private String getFieldValue(Part part) throws IOException {
        if (part == null) return null;
        java.io.InputStream is = part.getInputStream();
        return new String(is.readAllBytes(), "UTF-8");
    }
}
