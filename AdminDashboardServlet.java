package in.sp.main;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/adminDashboard")
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Session check if session is null or role is not admin so sent into login page
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("role") == null 
                || !session.getAttribute("role").equals("admin")) {
            resp.sendRedirect("index.html");
            return;
        }

        // set response type html
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        try {
        	// Driver Load
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Create Connection
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/login_demo", "root", "ses4321");

            // If Admin do not see in a list
            String sql = "SELECT * FROM register WHERE register_name != 'admin'";
            PreparedStatement ps = con.prepareStatement(sql);
            
            // execute a query
            ResultSet rs = ps.executeQuery();

            // HTML page start — head + CSS styles
            out.println("<!DOCTYPE html><html><head><meta charset='ISO-8859-1'>");
            out.println("<title>Admin Dashboard</title>");
            out.println("<style>");
            out.println("body { font-family: 'Segoe UI', sans-serif; background: linear-gradient(135deg, #71b7e6, #9b59b6); min-height: 100vh; margin: 0; padding: 30px; box-sizing: border-box; }");
            out.println("h2 { color: white; text-align: center; margin-bottom: 20px; }");
            out.println(".logout-div { text-align: right; margin-bottom: 10px; }");
            out.println("table { width: 100%; border-collapse: collapse; background: white; border-radius: 12px; overflow: hidden; box-shadow: 0 8px 24px rgba(0,0,0,0.15); }");
            out.println("th { background: linear-gradient(135deg, #9b59b6, #71b7e6); color: white; padding: 14px; text-align: left; font-size: 14px; }");
            out.println("td { padding: 12px 14px; font-size: 14px; color: #333; border-bottom: 1px solid #eee; }");
            out.println("tr:hover { background: #f9f4ff; }");
            out.println(".edit-btn { padding: 6px 14px; background: #9b59b6; color: white; border: none; border-radius: 4px; cursor: pointer; text-decoration: none; font-size: 13px; }");
            out.println(".delete-btn { padding: 6px 14px; background: #e74c3c; color: white; border: none; border-radius: 4px; cursor: pointer; text-decoration: none; font-size: 13px; margin-left: 6px; }");
            out.println(".logout-btn { padding: 8px 20px; background: #333; color: white; border: none; border-radius: 6px; cursor: pointer; text-decoration: none; font-size: 14px; font-weight: bold; }");
            out.println(".btn:hover { opacity: 0.9; }");
            out.println("</style></head><body>");
            out.println("<div class='logout-div'>");
            out.println("<a href='logout' class='logout-btn'>Logout</a>");
            out.println("</div>");
            out.println("<h2> Admin Dashboard All Users</h2>");
            out.println("<table>");
            out.println("<tr><th>ID</th><th>Name</th><th>Email</th><th>Gender</th><th>City</th><th>Actions</th></tr>");

            // ResultSet loop — one row for every user in a table 
            while (rs.next()) {
            	
            	// get a value for every column
                int id       = rs.getInt("register_id");
                String name  = rs.getString("register_name");
                String email = rs.getString("register_email");
                String gender = rs.getString("register_gender");
                String city  = rs.getString("register_city");

                // print data in a table row
                out.println("<tr>");
                out.println("<td>" + id + "</td>");
                out.println("<td>" + name + "</td>");
                out.println("<td>" + email + "</td>");
                out.println("<td>" + gender + "</td>");
                out.println("<td>" + city + "</td>");
                out.println("<td>");
                out.println("<a href='adminEdit?id=" + id + "' class='edit-btn'> Edit</a>");
                out.println("<a href='adminDelete?id=" + id + "' class='delete-btn'> Delete</a>");
                out.println("</td>");
                out.println("</tr>");
            }

            out.println("</table>");
            out.println("</body></html>");

            // Resources close — prevent memory leak 
            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {
            out.print("<h2 style='color:red;'>Error: " + e.getMessage() + "</h2>");
            e.printStackTrace();
        }
    }
}