# B2 — Portfolio Domain Status

Implemented:
- Portfolio CRUD with owner isolation.
- Canonical JSONB portfolio revisions.
- Revision numbers and schema version tracking.
- Draft/published/unpublished/archived lifecycle.
- Logical publish/unpublish (deployment infrastructure remains B5).
- Public published-portfolio endpoint.
- Slug validation and global uniqueness.
- V5 Flyway migration.
- Pagination for owned portfolio listing.

Not in B2: template marketplace/registry, template selection implementation, build workers, CDN deployment, BYO hosting, analytics, AI.
