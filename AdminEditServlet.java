package in.sp.main;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

@WebServlet("/adminEdit")
@MultipartConfig
public class AdminEditServlet extends HttpServlet {

    // GET - show edit form from selected user
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

    	// Session check if session is null or role is not admin so sent into login page
        HttpSession session = req.getSession(false);
        if (session == null || !session.getAttribute("role").equals("admin")) {
            resp.sendRedirect("index.html");
            return;
        }

        // Take user Id from URL e.g: adminEdit?id=5
        int userId = Integer.parseInt(req.getParameter("id"));

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        try {
        	// Driver Load
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Create connection
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/login_demo", "root", "ses4321");

            String sql = "SELECT * FROM register WHERE register_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String name   = rs.getString("register_name");
                String email  = rs.getString("register_email");
                String gender = rs.getString("register_gender");
                String city   = rs.getString("register_city");

                out.println("<!DOCTYPE html><html><head><meta charset='ISO-8859-1'>");
                out.println("<title>Admin Edit User</title>");
                out.println("<style>");
                out.println("body { font-family: 'Segoe UI', sans-serif; background: linear-gradient(135deg, #71b7e6, #9b59b6); min-height: 100vh; margin: 0; display: flex; justify-content: center; align-items: center; padding: 20px; }");
                out.println(".container { background: #fff; padding: 40px; border-radius: 12px; box-shadow: 0 8px 24px rgba(0,0,0,0.15); width: 360px; text-align: center; }");
                out.println("h2 { color: #333; margin-bottom: 24px; }");
                out.println(".input-group { text-align: left; margin-bottom: 18px; }");
                out.println("label { display: block; font-size: 14px; color: #666; margin-bottom: 6px; font-weight: 500; }");
                out.println("input, select { width: 100%; padding: 10px 12px; border: 1px solid #ccc; border-radius: 6px; font-size: 14px; box-sizing: border-box; }");
                out.println("input:focus, select:focus { border-color: #9b59b6; outline: none; }");
                out.println(".radio-options { display: flex; gap: 20px; }");
                out.println(".radio-options label { font-weight: normal; display: flex; align-items: center; gap: 6px; }");
                out.println(".btn { width: 100%; padding: 12px; border: none; border-radius: 6px; color: white; font-size: 15px; font-weight: bold; cursor: pointer; margin-top: 10px; }");
                out.println(".save-btn { background: linear-gradient(135deg, #9b59b6, #71b7e6); }");
                out.println(".back-btn { background: #555; text-decoration: none; display: inline-block; box-sizing: border-box; }");
                out.println("#result { margin-top: 16px; }");
                out.println("</style></head><body>");
                out.println("<div class='container'>");
                out.println("<h2>✏️ Edit User</h2>");
                out.println("<form id='editForm'>");
                out.println("<input type='hidden' name='userid' value='" + userId + "'>");

                out.println("<div class='input-group'><label>Username</label>");
                out.println("<input type='text' name='name1' value='" + name + "' required></div>");

                out.println("<div class='input-group'><label>Email</label>");
                out.println("<input type='email' name='email1' value='" + email + "' required></div>");

                out.println("<div class='input-group'><label>Gender</label>");
                out.println("<div class='radio-options'>");
                out.println("<label><input type='radio' name='gender1' value='Male' " + (gender.equals("Male") ? "checked" : "") + "> Male</label>");
                out.println("<label><input type='radio' name='gender1' value='Female' " + (gender.equals("Female") ? "checked" : "") + "> Female</label>");
                out.println("</div></div>");

                out.println("<div class='input-group'><label>City</label><select name='city1'>");
                String[] cities = {"Karachi","Lahore","Islamabad","Faisalabad","Multan","Peshawar","Quetta"};
                for (String c : cities) {
                    out.println("<option value='" + c + "' " + (city.equals(c) ? "selected" : "") + ">" + c + "</option>");
                }
                out.println("</select></div>");

                out.println("<button type='submit' class='btn save-btn'>💾 Save Changes</button>");
                out.println("</form>");
                out.println("<div id='result'></div>");
                out.println("<a href='adminDashboard' class='btn back-btn'>⬅ Back to Dashboard</a>");
                out.println("</div>");

                out.println("<script>");
                out.println("document.querySelector('#editForm').addEventListener('submit', function(e) {");
                out.println("  e.preventDefault();");
                out.println("  const formData = new FormData(this);");
                out.println("  fetch('adminEdit', { method: 'POST', body: formData })");
                out.println("  .then(r => r.text()).then(data => {");
                out.println("    document.getElementById('result').innerHTML = data;");
                out.println("  }).catch(() => {");
                out.println("    document.getElementById('result').innerHTML = '<h2 style=color:red>Error!</h2>';");
                out.println("  });");
                out.println("});");
                out.println("</script>");
                out.println("</body></html>");
            }

            // Resources close — prevent memory leak
            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {
            out.print("<h2 style='color:red;'>Error: " + e.getMessage() + "</h2>");
            e.printStackTrace();
        }
    }

    // POST - Update 
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || !session.getAttribute("role").equals("admin")) {
            resp.sendRedirect("index.html");
            return;
        }

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        int userId    = Integer.parseInt(getFieldValue(req.getPart("userid")));
        String myname   = getFieldValue(req.getPart("name1"));
        String myemail  = getFieldValue(req.getPart("email1"));
        String mygender = getFieldValue(req.getPart("gender1"));
        String mycity   = getFieldValue(req.getPart("city1"));

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/login_demo", "root", "ses4321");

            String sql = "UPDATE register SET register_name=?, register_email=?, register_gender=?, register_city=? WHERE register_id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, myname);
            ps.setString(2, myemail);
            ps.setString(3, mygender);
            ps.setString(4, mycity);
            ps.setInt(5, userId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                out.print("<h2 style='color:green;'>User Updated Successfully!</h2>");
                out.print("<a href='adminDashboard'>Back to Dashboard</a>");
            } else {
                out.print("<h2 style='color:red;'>Update Failed!</h2>");
            }

            ps.close();
            con.close();

        } catch (Exception e) {
            out.print("<h2 style='color:red;'>Error: " + e.getMessage() + "</h2>");
            e.printStackTrace();
        }
    }

    private String getFieldValue(Part part) throws IOException {
        if (part == null) return null;
        java.io.InputStream is = part.getInputStream();
        return new String(is.readAllBytes(), "UTF-8");
    }
}