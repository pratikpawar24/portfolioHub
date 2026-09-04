# PortfolioHub Backend — B5 + B6 Status

## B5 — Build & Publishing Foundation

- Added durable publishing jobs with QUEUED/RUNNING/SUCCEEDED/FAILED lifecycle.
- Publishing is asynchronous and starts after the request transaction commits.
- Added framework-neutral static artifact builder.
- Existing approved template artifacts may be supplied as a local directory or ZIP using `file:` or filesystem paths.
- Arbitrary user server-side code is never executed by Spring Boot.
- Canonical portfolio JSON is emitted as `/portfolio.json` and `/data/portfolio.json` for template consumption.
- Added deterministic artifact content hashing.
- Added safe ZIP extraction with traversal protection.
- Added deployment-provider abstraction.
- Added platform filesystem deployment provider.

## B6 — Platform Hosting

- Published sites are stored under a configurable platform root.
- Deployment uses a temporary directory and atomic rename where supported, avoiding partially published sites.
- Public portfolios are served at `/p/{slug}/`.
- Static assets are served safely with path traversal protection.
- HTML navigation gets SPA fallback to `index.html` when appropriate.
- `index.html` is `no-cache`; other assets receive a short public cache lifetime.
- Unpublish removes the platform-hosted site and changes status to `UNPUBLISHED`.

## API

- `POST /api/v1/portfolios/{portfolioId}/publish` → `202 Accepted` + publishing job.
- `GET /api/v1/portfolios/{portfolioId}/publish/jobs` → publishing history.
- `GET /api/v1/portfolios/publish/jobs/{jobId}` → job status.
- `POST /api/v1/portfolios/{portfolioId}/unpublish` → removes hosted site.
- `GET /p/{slug}/` → public hosted portfolio.

## Deliberate boundary

B5/B6 consume prebuilt static template artifacts. GitHub repository cloning, dependency installation, isolated builds, security scanning, preview builds and admin approval remain B7. SSR/server-runtime portfolios remain outside default platform hosting and belong to future external/isolated runtime adapters.
