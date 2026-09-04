ALTER TABLE templates ADD COLUMN framework VARCHAR(40);
ALTER TABLE templates ADD COLUMN derivation_type VARCHAR(20) NOT NULL DEFAULT 'ORIGINAL';

UPDATE templates t
SET framework = COALESCE(
    (
        SELECT v.manifest ->> 'framework'
        FROM template_versions v
        WHERE v.template_id = t.id
        ORDER BY v.created_at DESC
        LIMIT 1
    ),
    'html-static'
);

ALTER TABLE templates ALTER COLUMN framework SET NOT NULL;
ALTER TABLE templates ADD CONSTRAINT ck_templates_derivation_type CHECK (derivation_type IN ('ORIGINAL','FORK','REMIX'));
CREATE INDEX idx_templates_framework ON templates (framework);
CREATE INDEX idx_templates_creator_status ON templates (creator_user_id, status);
CREATE INDEX idx_templates_parent_derivation ON templates (parent_template_id, derivation_type);

CREATE TABLE template_likes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    template_id UUID NOT NULL REFERENCES templates(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_template_likes_user_template UNIQUE (user_id, template_id)
);
CREATE INDEX idx_template_likes_template ON template_likes (template_id);

CREATE TABLE template_favorites (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    template_id UUID NOT NULL REFERENCES templates(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_template_favorites_user_template UNIQUE (user_id, template_id)
);
CREATE INDEX idx_template_favorites_template ON template_favorites (template_id);
CREATE INDEX idx_template_favorites_user_created ON template_favorites (user_id, created_at DESC);

CREATE TABLE template_marketplace_stats (
    template_id UUID PRIMARY KEY REFERENCES templates(id) ON DELETE CASCADE,
    like_count BIGINT NOT NULL DEFAULT 0,
    favorite_count BIGINT NOT NULL DEFAULT 0,
    usage_count BIGINT NOT NULL DEFAULT 0,
    fork_count BIGINT NOT NULL DEFAULT 0,
    remix_count BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

INSERT INTO template_marketplace_stats(template_id)
SELECT id FROM templates
ON CONFLICT (template_id) DO NOTHING;

UPDATE template_marketplace_stats s
SET like_count = (SELECT COUNT(*) FROM template_likes l WHERE l.template_id = s.template_id),
    favorite_count = (SELECT COUNT(*) FROM template_favorites f WHERE f.template_id = s.template_id),
    usage_count = (
        SELECT COUNT(*)
        FROM portfolios p
        JOIN template_versions v ON v.id = p.active_template_version_id
        WHERE v.template_id = s.template_id
    ),
    fork_count = (
        SELECT COUNT(*) FROM templates child
        WHERE child.parent_template_id = s.template_id AND child.derivation_type = 'FORK'
    ),
    remix_count = (
        SELECT COUNT(*) FROM templates child
        WHERE child.parent_template_id = s.template_id AND child.derivation_type = 'REMIX'
    ),
    updated_at = NOW();

CREATE TABLE creator_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    bio VARCHAR(1000),
    avatar_url VARCHAR(500),
    website_url VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_creator_profiles_user UNIQUE (user_id)
);
