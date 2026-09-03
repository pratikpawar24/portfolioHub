CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE schema_metadata (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    schema_version VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_schema_metadata_created_at ON schema_metadata (created_at DESC);

INSERT INTO schema_metadata (schema_version) VALUES ('foundation-1');
