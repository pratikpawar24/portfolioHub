# B3 — Template Contract and Registry Status

Implemented:
- Relational template registry.
- Immutable template version records (no update endpoint).
- Manifest JSONB storage with required contract checks.
- Portfolio-schema compatibility validation.
- Public catalogue for active/public templates.
- Admin-only first-party template registration.
- Portfolio template selection and clearing.
- Parent/original lineage fields reserved for B4/B7 workflows.
- Foreign key from portfolio active template version to template version.
- V6 forward-only Flyway migration.

Not in B3:
- marketplace social discovery/ranking
- GitHub/community submissions
- sandboxed builds
- preview generation
- deployment workers/CDN
- BYO hosting
- AI
