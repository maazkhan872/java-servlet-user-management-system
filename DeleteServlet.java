package in.sp.main;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/delete")
public class DeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	
    	// Session check if session is null or role is not admin so sent into login page
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userid") == null) {
            resp.sendRedirect("index.html");
            return;
        }

     // Take user Id from URL e.g: adminDelete?id=5 
        int userid = (int) session.getAttribute("userid");

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        try {
        	// Driver load
            Class.forName("com.mysql.cj.jdbc.Driver");
            // create connection
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/login_demo", "root", "ses4321");

            // Delete Row
            String sql = "DELETE FROM register WHERE register_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userid);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                // Session destroy 
                session.invalidate();

                // Show confirmation page
                out.println("<!DOCTYPE html><html><head><title>Account Deleted</title>");
                out.println("<style>");
                out.println("body { font-family: 'Segoe UI', sans-serif; background: linear-gradient(135deg, #71b7e6, #9b59b6); height: 100vh; margin: 0; display: flex; justify-content: center; align-items: center; }");
                out.println(".container { background: #fff; padding: 40px; border-radius: 12px; box-shadow: 0 8px 24px rgba(0,0,0,0.15); width: 350px; text-align: center; }");
                out.println("h2 { color: #e74c3c; } p { color: #666; margin-bottom: 24px; }");
                out.println(".btn { padding: 12px 24px; background: linear-gradient(135deg, #9b59b6, #71b7e6); border: none; border-radius: 6px; color: white; font-size: 15px; font-weight: bold; cursor: pointer; text-decoration: none; }");
                out.println("</style></head><body>");
                out.println("<div class='container'>");
                out.println("<h2> Account Deleted!</h2>");
                out.println("<p>Your account has been permanently deleted.</p>");
                out.println("<a href='index.html' class='btn'>Go to Login Page</a>");
                out.println("</div></body></html>");
            } else {
                out.print("<h2 style='color:red;'>Delete Failed!</h2>");
            }

            ps.close();
            con.close();

        } catch (Exception e) {
            out.print("<h2 style='color:red;'>Error: " + e.getMessage() + "</h2>");
            e.printStackTrace();
        }
    }
}