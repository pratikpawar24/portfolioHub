ALTER TABLE portfolios DROP CONSTRAINT IF EXISTS ck_portfolios_status;
ALTER TABLE portfolios ADD CONSTRAINT ck_portfolios_status
    CHECK (status IN ('DRAFT','PUBLISHING','PUBLISHED','PUBLISH_FAILED','UNPUBLISHED','ARCHIVED'));

CREATE TABLE publishing_jobs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    portfolio_id UUID NOT NULL REFERENCES portfolios(id) ON DELETE CASCADE,
    revision_id UUID NOT NULL REFERENCES portfolio_revisions(id) ON DELETE RESTRICT,
    template_version_id UUID REFERENCES template_versions(id) ON DELETE RESTRICT,
    status VARCHAR(20) NOT NULL DEFAULT 'QUEUED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    artifact_path VARCHAR(1000),
    public_path VARCHAR(500),
    content_hash VARCHAR(64),
    error_message VARCHAR(4000),
    CONSTRAINT ck_publishing_jobs_status CHECK (status IN ('QUEUED','RUNNING','SUCCEEDED','FAILED'))
);

CREATE INDEX idx_publishing_jobs_portfolio_created ON publishing_jobs (portfolio_id, created_at DESC);
CREATE INDEX idx_publishing_jobs_status_created ON publishing_jobs (status, created_at DESC);
CREATE INDEX idx_publishing_jobs_revision ON publishing_jobs (revision_id);
