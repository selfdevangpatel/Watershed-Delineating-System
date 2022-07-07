package email;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailUtil {

    public static boolean sendMail(String uname, String recipient, int OTP) throws MessagingException {

        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        String myEmail = "yourEmailAddress";
        String emPass = "yourPassword";

        Session session = Session.getInstance(properties, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myEmail, emPass);
            }
        });

        Message message = prepareMessage(uname, session, myEmail, recipient, OTP);
        assert message != null;
        Transport.send(message);
        System.out.println("Message sent successfully!");

        return true;
    }

    private static Message prepareMessage(String uname, Session session, String myEmail, String recipient, int OTP) {

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(myEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject("Reset password - Watershed Delineating System");
            message.setText("Dear " + uname + ",\n\n" + "Your OTP for resetting password is " + OTP + "." +
                            "\nPlease note that the OTP will only be valid for the next 5 minutes." +
                            "\n\nThank you.");

            return message;
        } catch (Exception ex) {
            Logger.getLogger(EmailUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}