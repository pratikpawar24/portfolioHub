# B2 Update Changelog

Phase: B2 — Portfolio Domain

This ZIP contains only files added or modified for B2.

- Portfolio CRUD with authenticated ownership boundaries.
- Immutable revisioned portfolio content in PostgreSQL JSONB.
- Canonical Portfolio Schema v1.0 validation.
- Draft/published/unpublished/archived lifecycle.
- Expected revision checks and framework-level optimistic locking.
- Public published-only portfolio endpoint.
- Flyway V3 migration.
- B2 unit and Testcontainers integration tests.
- Frontend synchronization contract.

Runtime verification: Maven/Docker are unavailable in the authoring environment; run `mvn clean verify` in local/CI with Docker available.
