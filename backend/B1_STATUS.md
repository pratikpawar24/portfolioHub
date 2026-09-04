# PortfolioHub Backend — B1 Status

B1 Authentication & User Domain is implemented in this repository baseline.

## Included
- Java 21 / Spring Boot 4.1.1
- PostgreSQL + Flyway
- Redis
- User registration/login
- JWT access tokens
- Rotating opaque refresh tokens
- Logout/revocation
- Role-based authorization baseline
- Testcontainers-based integration test setup
- GitHub Actions CI

## Verification
The source tree has been assembled as a single coherent B0+B1 baseline. Maven/Docker runtime verification must be performed in the developer environment/CI because Maven is not installed in the build environment used to package this archive.
