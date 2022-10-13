package model;

import hibernate.ApplicationSetting;
import hibernate.HiberUtil;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Email {

    private static String from;
    private static Properties p = System.getProperties();
    private static Authenticator auth;

    static {
        p.put("mail.smtp.auth", "true");
        p.put("mail.smtp.starttls.enable", "true");
        init();
    }

    public static void send(String to, String subject, String content) throws MessagingException {
        
        Session s = Session.getInstance(p, auth);
        MimeMessage email = new MimeMessage(s);
        email.setFrom(from);
        email.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        email.setRecipients(Message.RecipientType.TO, to);
        email.setSubject(subject);

        String htmlContent = content;

        email.setContent(htmlContent, "text/html");
        Transport.send(email);
    }
    
    public static void init() {
        org.hibernate.Session s = HiberUtil.getSessionFactory().openSession();
        String smtp_host = ((ApplicationSetting) s.load(ApplicationSetting.class, "smtp_host")).getValue();
        String smtp_port = ((ApplicationSetting) s.load(ApplicationSetting.class, "smtp_port")).getValue();
        String smtp_sender = ((ApplicationSetting) s.load(ApplicationSetting.class, "smtp_sender")).getValue();
        String smtp_password = ((ApplicationSetting) s.load(ApplicationSetting.class, "smtp_password")).getValue();
        
        from = smtp_sender;

        auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                PasswordAuthentication pa = new PasswordAuthentication(from, smtp_password);
                return pa;
            }
        };
        p.put("mail.smtp.host", smtp_host);
        p.put("mail.smtp.port", smtp_port);
        s.close();
    }
}
