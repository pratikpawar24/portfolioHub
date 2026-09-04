# Test Fix 2

Changed files only:

- `pom.xml`
- `src/test/java/com/portfoliohub/common/web/HealthControllerTest.java`
- `docs/TEST_COMPATIBILITY_FIX_2.md`

Fixes:
- Missing `JwtService` in the `@WebMvcTest` application context.
- Testcontainers 1.21.3 / Docker API 1.32 incompatibility by pinning Testcontainers to 1.21.4.
