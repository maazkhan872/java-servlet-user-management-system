
package in.sp.main;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

@WebFilter("/dashboard/*")   // jo bhi URLs protect karni hain unka pattern
public class SessionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("SESSION_TOKEN")) {
                    token = c.getValue();
                }
            }
        }

        if (token != null && RedisSessionManager.isValidSession(token)) {
            RedisSessionManager.refreshSession(token); // sliding expiry
            request.setAttribute("userId", RedisSessionManager.getUserId(token));
            request.setAttribute("role", RedisSessionManager.getRole(token));
            chain.doFilter(req, res); // aage jane do
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp?expired=true");
        }
    }
}