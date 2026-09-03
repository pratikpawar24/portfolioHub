CREATE TABLE portfolios (
    id UUID PRIMARY KEY,
    owner_user_id UUID NOT NULL,
    title VARCHAR(80) NOT NULL,
    slug VARCHAR(80) NOT NULL,
    status VARCHAR(20) NOT NULL,
    active_template_version_id UUID NULL,
    current_draft_revision_id UUID NULL,
    published_revision_id UUID NULL,
    published_at TIMESTAMPTZ NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_portfolios_owner FOREIGN KEY (owner_user_id) REFERENCES users(id),
    CONSTRAINT uk_portfolios_owner_slug UNIQUE (owner_user_id, slug),
    CONSTRAINT ck_portfolios_status CHECK (status IN ('DRAFT', 'PUBLISHED', 'UNPUBLISHED', 'ARCHIVED'))
);

CREATE INDEX idx_portfolios_owner_user_id ON portfolios(owner_user_id);
CREATE INDEX idx_portfolios_status ON portfolios(status);

CREATE TABLE portfolio_revisions (
    id UUID PRIMARY KEY,
    portfolio_id UUID NOT NULL,
    revision_number BIGINT NOT NULL,
    schema_version VARCHAR(20) NOT NULL,
    content JSONB NOT NULL,
    created_by UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_portfolio_revisions_portfolio FOREIGN KEY (portfolio_id) REFERENCES portfolios(id) ON DELETE CASCADE,
    CONSTRAINT fk_portfolio_revisions_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT uk_portfolio_revisions_number UNIQUE (portfolio_id, revision_number)
);

CREATE INDEX idx_portfolio_revisions_portfolio_revision ON portfolio_revisions(portfolio_id, revision_number DESC);
CREATE INDEX idx_portfolio_revisions_created_by ON portfolio_revisions(created_by);

ALTER TABLE portfolios
    ADD CONSTRAINT fk_portfolios_current_draft_revision
        FOREIGN KEY (current_draft_revision_id) REFERENCES portfolio_revisions(id),
    ADD CONSTRAINT fk_portfolios_published_revision
        FOREIGN KEY (published_revision_id) REFERENCES portfolio_revisions(id);
