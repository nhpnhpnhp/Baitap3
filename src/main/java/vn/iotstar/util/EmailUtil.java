package vn.iotstar.util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EmailUtil {

    private static Properties loadMailConfig() {
        Properties props = new Properties();
        String customPath = System.getProperty("baitap2.mail.properties");
        String catalinaBase = System.getProperty("catalina.base");

        File[] candidates = new File[] {
                customPath != null ? new File(customPath) : null,
                catalinaBase != null ? new File(catalinaBase, "conf/mail.properties") : null,
                catalinaBase != null ? new File(catalinaBase, "../mail.properties") : null,
                new File(".runtime/mail.properties"),
                new File("mail.properties")
        };

        for (File file : candidates) {
            if (file != null && file.isFile()) {
                try (InputStream in = new FileInputStream(file)) {
                    props.load(in);
                    break;
                } catch (IOException ignored) {
                }
            }
        }

        overrideFromEnv(props, "MAIL_HOST");
        overrideFromEnv(props, "MAIL_PORT");
        overrideFromEnv(props, "MAIL_USERNAME");
        overrideFromEnv(props, "MAIL_PASSWORD");
        overrideFromEnv(props, "MAIL_FROM");
        overrideFromEnv(props, "MAIL_AUTH");
        overrideFromEnv(props, "MAIL_STARTTLS");

        return props;
    }

    private static void overrideFromEnv(Properties props, String key) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) {
            props.setProperty(key, sys.trim());
            return;
        }
        String env = System.getenv(key);
        if (env != null && !env.isBlank()) {
            props.setProperty(key, env.trim());
        }
    }

    public static boolean sendOtpEmail(String toEmail, String subject, String body) {
        Properties cfg = loadMailConfig();
        String host = cfg.getProperty("MAIL_HOST", "smtp.gmail.com");
        String port = cfg.getProperty("MAIL_PORT", "587");
        String username = cfg.getProperty("MAIL_USERNAME", "");
        String password = cfg.getProperty("MAIL_PASSWORD", "");
        String from = cfg.getProperty("MAIL_FROM", username);
        boolean auth = Boolean.parseBoolean(cfg.getProperty("MAIL_AUTH", "true"));
        boolean starttls = Boolean.parseBoolean(cfg.getProperty("MAIL_STARTTLS", "true"));

        if (auth && (username.isBlank() || password.isBlank() || password.startsWith("REPLACE_WITH"))) {
            System.err.println("Chưa cấu hình MAIL_USERNAME / MAIL_PASSWORD để gửi email.");
            return false;
        }
        if (from.isBlank()) {
            from = "no-reply@localhost";
        }

        Properties mailProps = new Properties();
        mailProps.put("mail.smtp.host", host);
        mailProps.put("mail.smtp.port", port);
        mailProps.put("mail.smtp.auth", String.valueOf(auth));
        mailProps.put("mail.smtp.starttls.enable", String.valueOf(starttls));
        mailProps.put("mail.smtp.connectiontimeout", "5000");
        mailProps.put("mail.smtp.timeout", "5000");

        Session session;
        if (auth) {
            final String user = username;
            final String pass = password;
            session = Session.getInstance(mailProps, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, pass);
                }
            });
        } else {
            session = Session.getInstance(mailProps);
        }

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject, "UTF-8");
            message.setText(body, "UTF-8");
            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            System.err.println("Gửi email thất bại: " + e.getMessage());
            return false;
        }
    }
}
