CREATE TABLE templates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    creator_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    slug VARCHAR(100) NOT NULL,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(2000),
    category VARCHAR(60),
    license VARCHAR(100) NOT NULL,
    repository_url VARCHAR(500),
    visibility VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    parent_template_id UUID REFERENCES templates(id) ON DELETE SET NULL,
    original_template_id UUID REFERENCES templates(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_templates_visibility CHECK (visibility IN ('PUBLIC','PRIVATE')),
    CONSTRAINT ck_templates_status CHECK (status IN ('DRAFT','ACTIVE','ARCHIVED'))
);
CREATE UNIQUE INDEX uk_templates_slug_lower ON templates (LOWER(slug));
CREATE INDEX idx_templates_status_visibility ON templates (status, visibility);
CREATE INDEX idx_templates_category ON templates (category);
CREATE INDEX idx_templates_parent ON templates (parent_template_id);

CREATE TABLE template_versions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    template_id UUID NOT NULL REFERENCES templates(id) ON DELETE CASCADE,
    version VARCHAR(30) NOT NULL,
    manifest JSONB NOT NULL,
    schema_min VARCHAR(32) NOT NULL,
    schema_max VARCHAR(32) NOT NULL,
    source_reference VARCHAR(500),
    artifact_reference VARCHAR(500),
    preview_reference VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_template_versions_template_version UNIQUE (template_id, version),
    CONSTRAINT ck_template_versions_status CHECK (status IN ('DRAFT','APPROVED','ARCHIVED'))
);
CREATE INDEX idx_template_versions_template ON template_versions (template_id, version);
CREATE INDEX idx_template_versions_status ON template_versions (status);

ALTER TABLE portfolios
    ADD CONSTRAINT fk_portfolios_active_template_version
    FOREIGN KEY (active_template_version_id)
    REFERENCES template_versions(id)
    ON DELETE SET NULL;
