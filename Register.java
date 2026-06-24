package in.sp.main;

import java.io.IOException;
import javax.servlet.annotation.MultipartConfig;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/register")
@MultipartConfig
public class Register extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	
    	// To show a sign up page when someone direct hit this url /register from browser
        req.getRequestDispatcher("signup.html").forward(req, resp);
    }
   

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        /*
        // Fetch data from Form
        String myname   = req.getParameter("name1");
        String myemail  = req.getParameter("email1");
        String mypass   = req.getParameter("pass1");
        String mygender = req.getParameter("gender1");
        String mycity   = req.getParameter("city1");*/

        String myname   = getFieldValue(req.getPart("name1"));
        String myemail  = getFieldValue(req.getPart("email1"));
        String mypass   = getFieldValue(req.getPart("pass1"));
        String mygender = getFieldValue(req.getPart("gender1"));
        String mycity   = getFieldValue(req.getPart("city1"));

        // Check on Console (For debugging)
        System.out.println("Name: " + myname);
        System.out.println("Email: " + myemail);
        System.out.println("Gender: " + mygender);
        System.out.println("City: " + mycity);

        // Performed Hashing
        String hashedPassword = BCrypt.hashpw(mypass, BCrypt.gensalt());
        System.out.println("Hashed Password: " + hashedPassword);

        try {
            // Driver load 
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create Connection 
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/login_demo", "root", "ses4321");

            // PreparedStatement (id auto-increment)
            String sql = "insert into register (register_name, register_email, register_password, register_gender, register_city) values (?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, myname);
            ps.setString(2, myemail);
            ps.setString(3, hashedPassword);   // Hash Password
            ps.setString(4, mygender);
            ps.setString(5, mycity);

            // Execute query
            int rows = ps.executeUpdate();

            if (rows > 0) {
                out.print("<h2 style='color:green;'>Registration Successful!</h2>");
                out.print("<p>Welcome, " + myname + ". You can now login.</p>");
                out.print("<a href='index.html'>Go to Login Page</a>");
            } else {
                out.print("<h2 style='color:red;'>Registration Failed!</h2>");
            }

            ps.close();
            con.close();

        } catch (Exception e) {
            out.print("<h2 style='color:red;'>Error occurred!</h2>");
            out.print("<p>" + e.getMessage() + "</p>");
            e.printStackTrace();
        }
    }
    
    private String getFieldValue(Part part) throws IOException {
    	
    	// if part is null (field missing) so return a null — save from NullPointerException
        if (part == null) return null;
        
        // Get the InputStream from the Part it
        java.io.InputStream is = part.getInputStream();
        
        // Convert the bytes from the InputStream into a String using UTF-8 encoding
        // readAllBytes() reads all bytes from the InputStream at once
        return new String(is.readAllBytes(), "UTF-8");
    }
}