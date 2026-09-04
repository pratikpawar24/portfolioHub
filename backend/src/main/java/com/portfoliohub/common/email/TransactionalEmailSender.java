package com.portfoliohub.common.email;

public interface TransactionalEmailSender {
    void sendEmailVerificationOtp(String recipientEmail, String displayName, String otp, long expiryMinutes);
}
