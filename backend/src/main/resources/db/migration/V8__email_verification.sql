ALTER TABLE users
    ADD COLUMN email_verified_at TIMESTAMPTZ;

CREATE TABLE email_verification_otps (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    code_hash VARCHAR(128) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    used_at TIMESTAMPTZ,
    attempts INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_email_verification_otps_attempts CHECK (attempts >= 0)
);

CREATE INDEX idx_email_verification_otps_user_created
    ON email_verification_otps (user_id, created_at DESC);

CREATE INDEX idx_email_verification_otps_expires_at
    ON email_verification_otps (expires_at);
