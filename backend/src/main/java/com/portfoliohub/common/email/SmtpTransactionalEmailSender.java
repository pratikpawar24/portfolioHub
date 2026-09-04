package com.portfoliohub.common.email;

import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!test")
public class SmtpTransactionalEmailSender implements TransactionalEmailSender {
    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final String fromName;

    public SmtpTransactionalEmailSender(
            JavaMailSender mailSender,
            @Value("${app.email.from-address}") String fromAddress,
            @Value("${app.email.from-name:PortfolioHub}") String fromName) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.fromName = fromName;
    }

    @Override
    public void sendEmailVerificationOtp(String recipientEmail, String displayName, String otp, long expiryMinutes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(fromAddress, fromName);
            helper.setTo(recipientEmail);
            helper.setSubject("Verify your PortfolioHub email");
            helper.setText(html(displayName, otp, expiryMinutes), true);
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException ex) {
            throw new IllegalStateException("Unable to create email verification message", ex);
        }
    }

    private String html(String displayName, String otp, long expiryMinutes) {
        String safeName = escape(displayName == null || displayName.isBlank() ? "there" : displayName);
        return """
                <!doctype html>
                <html>
                <body style="margin:0;background:#f6f8fb;font-family:Arial,sans-serif;color:#172033;">
                  <div style="max-width:560px;margin:32px auto;padding:32px;background:#ffffff;border-radius:12px;">
                    <h2 style="margin-top:0;">Verify your PortfolioHub email</h2>
                    <p>Hi %s,</p>
                    <p>Use the verification code below to finish creating your PortfolioHub account.</p>
                    <div style="font-size:32px;font-weight:700;letter-spacing:8px;padding:20px 0;text-align:center;">%s</div>
                    <p>This code expires in %d minutes and can only be used once.</p>
                    <p style="color:#667085;font-size:13px;">If you did not create this account, you can safely ignore this email.</p>
                  </div>
                </body>
                </html>
                """.formatted(safeName, otp, expiryMinutes);
    }

    private String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
}
