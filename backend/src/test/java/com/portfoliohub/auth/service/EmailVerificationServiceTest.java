package com.portfoliohub.auth.service;

import com.portfoliohub.auth.entity.EmailVerificationOtp;
import com.portfoliohub.auth.entity.User;
import com.portfoliohub.auth.repository.EmailVerificationOtpRepository;
import com.portfoliohub.auth.repository.UserRepository;
import com.portfoliohub.common.email.TransactionalEmailSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    @Mock UserRepository userRepository;
    @Mock EmailVerificationOtpRepository otpRepository;
    @Mock TransactionalEmailSender emailSender;

    @Test
    void issueForGeneratesSixDigitOtpAndSendsIt() {
        UUID userId = UUID.randomUUID();
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        when(user.getEmail()).thenReturn("user@example.com");
        when(user.getDisplayName()).thenReturn("Test User");
        when(user.isEmailVerified()).thenReturn(false);
        when(otpRepository.findTopByUserIdAndUsedAtIsNullOrderByCreatedAtDesc(userId)).thenReturn(Optional.empty());

        EmailVerificationService service = new EmailVerificationService(
                userRepository, otpRepository, emailSender, 10, 60, 5, "test-secret");

        service.issueFor(user);

        ArgumentCaptor<String> otpCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailSender).sendEmailVerificationOtp(eq("user@example.com"), eq("Test User"), otpCaptor.capture(), eq(10L));
        assertThat(otpCaptor.getValue()).matches("\\d{6}");
        verify(otpRepository).save(any(EmailVerificationOtp.class));
    }
}
