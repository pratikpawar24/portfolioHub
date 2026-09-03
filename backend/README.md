# PortfolioHub Backend

Spring Boot backend for PortfolioHub.

## Current phase

B0 — Foundation.

## Stack

- Java 21
- Spring Boot 4.1.1
- Spring MVC
- Spring Data JPA
- PostgreSQL
- Flyway
- Redis
- Spring Security baseline
- OpenAPI / Swagger UI
- JUnit / Testcontainers

## Local development

1. Start dependencies:

```bash
docker compose up -d
```

2. Run the application:

```bash
./mvnw spring-boot:run
```

or:

```bash
mvn spring-boot:run
```

3. Check:

- `GET /api/v1/system/ping`
- `GET /actuator/health`
- `/swagger-ui.html`

## Architecture rule

The application starts as a modular monolith. Expensive or untrusted build/deployment work will run outside the API process through isolated workers.


## CI

Backend CI is defined in `.github/workflows/backend-ci.yml`. It runs Java 21 + Maven `clean verify` on pushes to `main`/`develop` and pull requests to those branches. Integration tests use Testcontainers for PostgreSQL and Redis.

## GitHub Actions

CI is maintained independently from application source code under `.github/workflows/`.
Backend and frontend have separate workflow files. The backend CI is active now; the frontend workflow template is activated when the frontend is integrated.

## B0 frontend/backend sync

The stable application reachability endpoint is `GET /api/v1/system/ping`. Client applications should use this endpoint rather than depending on the shape of Spring Boot Actuator health responses. See `docs/B0_FRONTEND_BACKEND_SYNC.md`.

## Current development status
- B0 Foundation: complete at the source/contract level.
- B1 Authentication & User Domain: implemented in this branch/package; local and CI verification required before merge.

### B1 endpoints
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/logout`
- `GET /api/v1/auth/me`
