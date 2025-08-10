package com.yanuar.service;

import com.yanuar.model.Contact;
import com.yanuar.util.SMTPConfig;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.List;
import java.util.Properties;
import java.util.function.BiConsumer;

public class EmailService {
    private final Session session;
    private final String from;

    public EmailService(SMTPConfig cfg) {
        String host = cfg.getHost();
        int port = cfg.getPort();
        String user = cfg.getUsername();
        String pass = cfg.getPassword();
        boolean useTls = cfg.isUseTls();
        this.from = (cfg.getFrom() == null || cfg.getFrom().isEmpty()) ? user : cfg.getFrom();

        // if password blank in config, fallback to ENV SMTP_PASSWORD
        if ((pass == null || pass.isEmpty())) {
            String env = System.getenv("SMTP_PASSWORD");
            if (env != null && !env.isEmpty()) pass = env;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));
        props.put("mail.smtp.auth", (user != null && !user.isEmpty()) ? "true" : "false");
        if (useTls) props.put("mail.smtp.starttls.enable", "true");

        Authenticator auth = null;
        if (user != null && !user.isEmpty()) {
            final String fu = user;
            final String fp = pass;
            auth = new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fu, fp);
                }
            };
        }
        session = Session.getInstance(props, auth);
    }

    public void sendBulk(List<Contact> contacts, String subject, String body, BiConsumer<Integer, Integer> progress) throws Exception {
        if (contacts == null || contacts.isEmpty()) return;
        int total = contacts.size();
        int done = 0;
        for (Contact c : contacts) {
            try {
                String to = c.getEmail();
                if (to == null || to.trim().isEmpty()) {
                    // skip
                } else {
                    sendSingle(to, replacePlaceholders(c, subject), replacePlaceholders(c, body));
                }
            } catch (Exception ex) {
                ex.printStackTrace(); // continue
            } finally {
                done++;
                if (progress != null) progress.accept(done, total);
            }
        }
    }

    private void sendSingle(String to, String subject, String htmlBody) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject == null ? "" : subject);
        message.setContent(htmlBody == null ? "" : htmlBody, "text/html; charset=utf-8");
        Transport.send(message);
    }

    private String replacePlaceholders(Contact c, String text) {
        if (text == null) return "";
        return text.replace("{NAMA}", c.getNama() == null ? "" : c.getNama())
                .replace("{CIFNO}", c.getCifno() == null ? "" : c.getCifno())
                .replace("{TOTAL_TABUNGAN}", c.getTotalTabunganRupiah() == null ? "" : c.getTotalTabunganRupiah())
                .replace("{TERBILANG}", c.getTotalTabunganTerbilang() == null ? "" : c.getTotalTabunganTerbilang());
    }
}
