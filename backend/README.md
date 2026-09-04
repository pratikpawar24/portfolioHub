# PortfolioHub — B1.1 Email Verification Patch

This patch adds email OTP verification using Brevo SMTP and makes email verification a prerequisite for password login.

## Environment
```bash
export BREVO_SMTP_HOST=smtp-relay.brevo.com
export BREVO_SMTP_PORT=587
export BREVO_SMTP_USERNAME='your-brevo-smtp-login'
export BREVO_SMTP_KEY='your-brevo-smtp-key'
export EMAIL_FROM_ADDRESS='no-reply@yourdomain.com'
export EMAIL_FROM_NAME='PortfolioHub'
export OTP_HASH_SECRET='use-a-long-random-secret'
```

Keep credentials out of Git. In production, provide them through the hosting platform secret manager/environment.

## API
- `POST /api/v1/auth/register` → creates account and triggers verification email.
- `POST /api/v1/auth/verify-email` → verifies `{email, otp}`.
- `POST /api/v1/auth/resend-verification` → resends OTP with abuse protection.
- `POST /api/v1/auth/login` → succeeds only after email verification.

Registration no longer returns access/refresh tokens; the frontend should route the user to the email verification screen after registration.
