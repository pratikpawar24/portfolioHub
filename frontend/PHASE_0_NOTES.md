# Frontend Phase 0 — Foundation

Per `ParallelPhasePlan.md`, Phase 0 frontend scope is: app shell, TypeScript
foundation, routing/layout, design tokens, typed API client, error/loading/
empty primitives. Nothing here depends on an endpoint beyond health.

## 1. Implemented routes/components

Routes (Next.js App Router, route groups don't affect the URL):
- `/` — landing page (real content)
- `/templates`, `/templates/[slug]` — honest empty states, no hardcoded cards
- `/login`, `/register` — stubs, deliberately deferred to Phase 1
- `/dashboard` (+ layout) — sidebar shell; overview page exercises the API client against `/actuator/health`
- `/dashboard/portfolio`, `/dashboard/portfolio/editor`, `/dashboard/portfolio/templates` — stubs (Phase 2/3)
- `/dashboard/deployments` — stub (Phase 5)
- `/dashboard/settings`, `/dashboard/settings/connections` — stubs (Phase 8, V3)
- `/p/[username]` — public portfolio route, no dashboard chrome, works without a session

`/admin` and `/explore` were intentionally **not** scaffolded: `/admin` needs
a backend authorization model that doesn't exist yet, and `/explore`
(marketplace/social discovery) is V2 per `VERSION_ROADMAP.md`.

Shared components:
- `components/layout/`: `SiteHeader`, `SiteFooter`, `DashboardNav`, `SkipLink`
- `components/states/`: `LoadingState`, `EmptyState`, `ErrorState` (covers unauthorized/forbidden/not_found/conflict/validation/rate_limited/server/network)
- `components/ui/`: `Button`, `Badge`
- `lib/api/`: `config.ts`, `types.ts`, `client.ts` (`apiFetch<T>`, `ApiError`, `getHealth`)

## 2. API endpoints consumed

- `GET {NEXT_PUBLIC_API_BASE_URL}/actuator/health` — used on the dashboard
  overview page only, to demonstrate the Phase 0 sync gate. **Path is not
  confirmed against a real OpenAPI/actuator config** — update
  `HEALTH_PATH` in `lib/api/config.ts` once the backend chat publishes it.

Nothing else calls the backend yet. `apiFetch<T>` exists and classifies
401/403/404/409/422/429/5xx/network, but no endpoint is wired to it —
there's no confirmed OpenAPI contract for portfolios/templates/auth yet.

## 3. Environment variables required

- `NEXT_PUBLIC_API_BASE_URL` — see `.env.example`. Defaults to
  `http://localhost:8080` if unset.

## 4. Known backend dependencies

- Actuator/health endpoint path, to confirm `HEALTH_PATH`.
- OpenAPI contract for auth (Phase 1), portfolio CRUD (Phase 2), and the
  template registry (Phase 3) — none of this exists yet per `Memory.md`
  (backend is at B0 only).

## 5. Tests/checks performed

- `npm run build` — production build succeeds, all routes compile.
- `npm run lint` — no errors.
- Manual keyboard-only pass: skip link, visible focus on all interactive
  elements, dashboard nav reachable by keyboard.
- No automated tests yet — nothing here has business logic worth testing
  beyond `apiFetch`'s error classification, which Phase 1 should cover
  once a real endpoint exists to test against.

## 6. Known limitations

- Every screen past the landing page is an intentional empty/stub state —
  there is no fake data anywhere, per the "never hard-code" rules.
- No dark mode — a single considered light theme only; revisit if the
  product actually needs one.
- `react-hook-form` / `zod` / `@tanstack/react-query` are **not**
  installed yet — nothing in Phase 0 has a form or server-state need that
  justifies them. Add them when Phase 1 auth forms need real validation.
- Fonts (Space Grotesk, IBM Plex Sans, IBM Plex Mono) are self-hosted via
  `@fontsource/*` npm packages rather than `next/font/google`. This
  environment's network egress couldn't reach `fonts.googleapis.com` at
  build time, which surfaced a real build failure — self-hosting fixes it
  and also removes a runtime dependency on Google's font CDN in
  production, so it's staying this way regardless of whether your build
  environment can reach Google Fonts.

## 7. Changes that require backend contract updates

- Confirm the actual health-check path and response shape.
- Publish OpenAPI for auth before Phase 1 frontend work starts, per the
  sync gate in `ParallelPhasePlan.md`.
