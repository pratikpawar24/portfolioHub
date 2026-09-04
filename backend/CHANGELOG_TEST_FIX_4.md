# Test Fix 4 — Testcontainers version not applied

Changed files only:

- `pom.xml`
- `CHANGELOG_TEST_FIX_4.md`

## Problem
The latest test output still reports:

`Testcontainers version: 1.21.3`

The previous patch intended to change the version, but the cumulative project still contains `1.21.3`.

## Change
Pinned Testcontainers to `1.21.4` in the cumulative Maven project so the existing 1.x test dependencies remain compatible with the current test source.

## Next verification
Run:

```bash
mvn clean test
```

The test log must report:

`Testcontainers version: 1.21.4`

If Docker then still rejects the client API version, investigate the local Docker CLI/daemon environment separately rather than changing application code.
