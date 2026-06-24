package in.sp.main;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/welcome")
public class WelcomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

    	// Session check if session is null or role is not admin so sent into login page
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            resp.sendRedirect("index.html");
            return;
        }
     
        // Retrieve the username from the session
        String username = (String) session.getAttribute("username");
  
        // Set the response content type to HTML
        resp.setContentType("text/html");
        
        // Get the PrintWriter object to send HTML output to the browser
        PrintWriter out = resp.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html><head><meta charset='ISO-8859-1'><title>Welcome</title>");
        out.println("<style>");
        out.println("body { font-family: 'Segoe UI', sans-serif; background: linear-gradient(135deg, #71b7e6, #9b59b6); height: 100vh; margin: 0; display: flex; justify-content: center; align-items: center; }");
        out.println(".welcome-container { background: #fff; padding: 40px; border-radius: 12px; box-shadow: 0 8px 24px rgba(0,0,0,0.15); width: 350px; text-align: center; }");
        out.println("h2 { color: #333; margin-bottom: 10px; }");
        out.println("p { color: #666; margin-bottom: 24px; }");
        out.println(".btn { width: 100%; padding: 12px; border: none; border-radius: 6px; color: white; font-size: 15px; font-weight: bold; cursor: pointer; text-decoration: none; display: inline-block; box-sizing: border-box; margin-top: 10px; }");
        out.println(".edit-btn { background: linear-gradient(135deg, #9b59b6, #71b7e6); }");
        out.println(".delete-btn { background: linear-gradient(135deg, #e74c3c, #c0392b); }");
        out.println(".logout-btn { background: linear-gradient(135deg, #555, #333); }");
        out.println(".btn:hover { opacity: 0.9; }");
        out.println("</style></head><body>");
        out.println("<div class='welcome-container'>");
        out.println("<h2>Welcome, " + username + "! </h2>");
        out.println("<p>You have successfully logged in.</p>");
        out.println("<a href='edit' class='btn edit-btn'> Edit Profile</a>");
        out.println("<a href='delete' class='btn delete-btn'> Delete Account</a>");
        out.println("<a href='logout' class='btn logout-btn'>Logout</a>");
        out.println("</div>");
        out.println("</body></html>");
    }
}