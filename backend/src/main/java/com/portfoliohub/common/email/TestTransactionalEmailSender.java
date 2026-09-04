package com.portfoliohub.common.email;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Test-only transactional email implementation. It deliberately does not send
 * network traffic; application-context tests can exercise authentication and
 * verification flows without requiring Brevo SMTP credentials.
 */
@Service
@Profile("test")
public class TestTransactionalEmailSender implements TransactionalEmailSender {

    @Override
    public void sendEmailVerificationOtp(String recipientEmail, String displayName, String otp, long expiryMinutes) {
        // Intentionally no-op. Service-level OTP tests mock TransactionalEmailSender
        // when they need to inspect the generated code.
    }
}
