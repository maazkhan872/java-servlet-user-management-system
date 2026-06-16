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

@WebServlet("/adminDelete")
public class AdminDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

    	// Session check if session is null or role is not admin so sent into login page
        HttpSession session = req.getSession(false);
        if (session == null || !session.getAttribute("role").equals("admin")) {
            resp.sendRedirect("index.html");
            return;
        }

        // Take user Id from URL e.g: adminDelete?id=5
        int userId = Integer.parseInt(req.getParameter("id"));

        try {
        	// Driver Load
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Create a connection
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/login_demo", "root", "ses4321");

            // fetch data from database
            String sql = "DELETE FROM register WHERE register_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            int rows = ps.executeUpdate();

            ps.close();
            con.close();

            if (rows > 0) {
            	
                // after delete return to dashboard
                resp.sendRedirect("adminDashboard");
            } else {
                resp.setContentType("text/html");
                PrintWriter out = resp.getWriter();
                out.print("<h2 style='color:red;'>Delete Failed!</h2>");
                out.print("<a href='adminDashboard'>Back</a>");
            }

        } catch (Exception e) {
            resp.setContentType("text/html");
            PrintWriter out = resp.getWriter();
            out.print("<h2 style='color:red;'>Error: " + e.getMessage() + "</h2>");
            e.printStackTrace();
        }
    }
}