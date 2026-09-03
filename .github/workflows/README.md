# GitHub Actions Workflows

CI workflow files live at the repository root under `.github/workflows/`.

Backend and frontend are intentionally separated by workflow file:

- `backend-ci.yml` — active backend build, tests, and verification.
- `frontend-ci.yml` — frontend workflow template; activate after the frontend is added and its package manager/build contract is finalized.

Do not place workflow YAML files in `.github/workflows/backend/` or `.github/workflows/frontend/`. GitHub Actions does not discover workflow files recursively from nested workflow directories.
