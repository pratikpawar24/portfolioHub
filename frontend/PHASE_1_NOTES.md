# Frontend Phase 1 — Authentication

Per `ParallelPhasePlan.md`, Phase 1 frontend scope is: registration, login,
logout, protected routes, account page. Sync gate: **"a real user can
register, authenticate, access protected resources and log out."**

**That sync gate is not met.** `Memory.md` shows backend at B0 only — B1
(auth) doesn't exist yet, so nothing here has been exercised against a
real API. Everything below is built and verified to the extent that's
possible without a backend: it compiles, type-checks, lints clean, and
renders correctly client-side against *simulated* success/error states.
It has not made a single real network round trip that returned real data.

## 1. Implemented routes/components

- `/login`, `/register` — real forms (`LoginForm`, `RegisterForm`),
  replacing the Phase 0 stubs
- `/dashboard/*` — now behind `RequireAuth`; redirects to `/login` if
  signed out, shows a connectivity error (not a redirect) if the API is
  simply unreachable
- `/dashboard/settings` — now a real account page, reading email/username
  from the session

New shared code:
- `lib/auth/` — `AuthContext` (`AuthProvider`, `useAuth`), `api.ts`
  (register/login/logout/getCurrentUser), `types.ts`
- `lib/validation/auth.ts` — Zod schemas for both forms (client-side UX
  only, not the source of truth — see §6)
- `lib/validation/applyServerFieldErrors.ts` — best-effort mapper from
  the error envelope's `details` to form fields
- `components/auth/` — `RequireAuth`, `LoginForm`, `RegisterForm`
- `components/ui/TextField.tsx` — accessible labeled input

## 2. API endpoints consumed

All four are **unconfirmed, best-guess paths** in `lib/api/config.ts`
(`AUTH_PATHS`), following Spring Security REST convention:
`POST /api/v1/auth/register`, `POST /api/v1/auth/login`,
`POST /api/v1/auth/logout`, `GET /api/v1/auth/me`.

Also unconfirmed: whether a CSRF token header is required alongside the
session cookie. `apiFetch` already sends `credentials: "include"`, but no
CSRF header is added yet — flagged in `config.ts`.

## 3. Environment variables required

Unchanged from Phase 0: `NEXT_PUBLIC_API_BASE_URL`. `.env.local` now
exists locally (gitignored) alongside the committed `.env.example`.

## 4. Known backend dependencies

- Real OpenAPI for the four auth endpoints above — request/response
  shapes, the actual error `details` shape, and the CSRF requirement all
  need confirming, not assuming.
- `AuthUser` shape (`id`, `email`, `username`) in `lib/auth/types.ts` is
  a guess — PRD.md §7.1 doesn't specify field names.
- Username/slug rules: only format (lowercase, url-safe, 3–30 chars) is
  validated client-side. Uniqueness and reserved-word blocking are
  deliberately left to the server (409/422), per "don't put business
  rules only in the frontend."

## 5. Tests/checks performed

- `npm run build` — succeeds, all 14 routes compile and type-check.
- `npm run lint` — clean.
- Live `next dev` smoke test: `/login` and `/register` render their
  fields; `/dashboard` correctly shows "Checking your session" during
  the (client-side) auth check and does not crash. No backend running,
  so the actual register/login/logout round trip is unverified — that's
  the real Phase 1 sync gate, still outstanding.
- No automated tests. Worth adding once a real backend exists:
  `applyServerFieldErrors` (shape-matching logic) and `RequireAuth`'s
  three-way branch (checking/error/anonymous) are the two places with
  actual logic worth covering.

## 6. Known limitations

- Password/email complexity rules in `lib/validation/auth.ts` (8-char
  minimum, etc.) are frontend UX conveniences, not the real policy —
  Requirements.md doesn't specify one. Don't treat these as the source
  of truth.
- `RequireAuth` clears local session state on logout even if the logout
  request itself fails (network/server error) — see the comment in
  `AuthContext.tsx`. Reasonable default, but means the UI could show
  "logged out" while a server-side session cookie technically survives a
  failed logout call.
- No password-reset or change-password UI. Not in the Phase 1 scope list
  ("registration, login, logout, protected routes, account page") and
  Requirements.md doesn't detail that flow, so it's left out rather than
  guessed at.

## 7. Changes that require backend contract updates

- Publish real OpenAPI for `/auth/register`, `/auth/login`,
  `/auth/logout`, `/auth/me` (paths, request/response bodies, status
  codes actually used).
- Confirm the error `details` array shape (currently assumed to be
  `{ field, message }[]`).
- Confirm whether CSRF header handling needs adding to `apiFetch`.
