# PortfolioHub Backend

PortfolioHub is a multi-tenant portfolio publishing platform. Users maintain structured portfolio content, choose or customize templates, publish a public portfolio, and later optionally connect their own hosting provider.

## Product versions

- **V1 — Portfolio Platform:** authentication, portfolio CRUD, template selection, theme customization, preview, responsive templates, publishing and platform hosting.
- **V2 — Template Ecosystem:** marketplace, GitHub template submissions, creator profiles, likes, usage, fork/remix, attribution and versioning.
- **V3 — Developer Integrations:** GitHub OAuth/import, resume upload, custom domains, analytics and optional BYO Vercel/Netlify/Cloudflare hosting.
- **V4 — AI:** resume-to-portfolio, writing assistance, critique, skill-gap analysis and recruiter optimization.
- **V5 — Ecosystem:** monetization, public API, plugins, organizations, university portfolios and recruiter tooling.

## Engineering phases

B0 → B1 → B2 → B3 → B4 → B5 → B6 → B7 → B8 → B9 → AI/Ecosystem

## Stack

- Java 21
- Spring Boot
- Spring MVC
- Spring Data JPA / Hibernate
- PostgreSQL
- Flyway
- Redis
- Spring Security
- OpenAPI / Swagger
- Maven
- Docker Compose
- GitHub Actions

## Local development

Prerequisites: Java 21, Maven 3.9+, Docker.

```bash
docker compose up -d
mvn spring-boot:run
```

The default local server runs on `http://localhost:8080`.

Useful endpoints:

- Health: `/actuator/health`
- API docs: `/swagger-ui.html`
- OpenAPI JSON: `/v3/api-docs`

## CI

`.github/workflows/backend-ci.yml` runs on pull requests and pushes to the main development branches. It provisions PostgreSQL and Redis as GitHub Actions services, then runs:

```bash
mvn -B -ntp verify
```

No production credentials or external hosting secrets belong in the repository. Those are added only when the deployment phase requires them.
