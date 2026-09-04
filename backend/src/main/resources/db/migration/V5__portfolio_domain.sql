CREATE TABLE portfolios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(120) NOT NULL,
    slug VARCHAR(80) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    active_template_version_id UUID,
    current_draft_revision_id UUID,
    published_revision_id UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    published_at TIMESTAMPTZ,
    CONSTRAINT ck_portfolios_status CHECK (status IN ('DRAFT','PUBLISHED','UNPUBLISHED','ARCHIVED'))
);
CREATE UNIQUE INDEX uk_portfolios_slug_lower ON portfolios (LOWER(slug));
CREATE INDEX idx_portfolios_owner_updated ON portfolios (owner_user_id, updated_at DESC);
CREATE INDEX idx_portfolios_status ON portfolios (status);
CREATE TABLE portfolio_revisions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    portfolio_id UUID NOT NULL REFERENCES portfolios(id) ON DELETE CASCADE,
    revision_number BIGINT NOT NULL CHECK (revision_number > 0),
    schema_version VARCHAR(32) NOT NULL,
    content JSONB NOT NULL,
    created_by UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_portfolio_revisions_number UNIQUE (portfolio_id, revision_number)
);
CREATE INDEX idx_portfolio_revisions_latest ON portfolio_revisions (portfolio_id, revision_number DESC);
ALTER TABLE portfolios ADD CONSTRAINT fk_portfolios_current_draft_revision FOREIGN KEY (current_draft_revision_id) REFERENCES portfolio_revisions(id) ON DELETE RESTRICT;
ALTER TABLE portfolios ADD CONSTRAINT fk_portfolios_published_revision FOREIGN KEY (published_revision_id) REFERENCES portfolio_revisions(id) ON DELETE RESTRICT;
