package in.sp.main;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailUtil {

    // Put you gmail and App password here 
    private static final String FROM_EMAIL = "maazkhank872@gmail.com";
    private static final String APP_PASSWORD = "rbro ejye allo wuhf"; // 16-digit app password

    public static void sendWelcomeEmail(String toEmail, String username) {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Welcome to Our Portal!");
            message.setText("Hi " + username + ",\n\nYour account has been created successfully!\n\nThank you for signing up.");

            Transport.send(message);
            System.out.println("Email sent successfully to: " + toEmail);

        } catch (MessagingException e) {
            System.out.println("Email sending failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}