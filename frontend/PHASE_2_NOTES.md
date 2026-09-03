# Frontend Phase 2 — Portfolio CMS

Per `ParallelPhasePlan.md`, Phase 2 frontend scope is: editor,
autosave/manual-save UX as contract allows, section management,
validation. Sync gate: **"User can create and edit one complete
portfolio using the canonical API contract."**

**Not met, same as Phase 1** — no backend exists to create/edit against
(`Memory.md` still shows B0 only). Built to the same standard as before:
real, working frontend logic against a clearly-flagged best-guess
contract, verified by build/lint/live-render, not by an actual save
round trip.

## 1. Implemented routes/components

- `/dashboard/portfolio/editor` — the real editor, replacing the stub
- `/dashboard/portfolio` — real summary (name, headline, section counts)
  instead of a hardcoded empty state
- `/dashboard` — portfolio card now reuses the same fetch logic instead
  of a static "No portfolio yet"; also fixed stale "Ready for Phase 1"
  copy left over from the last phase

New code:
- `lib/portfolio/` — `types.ts`, `api.ts`, `formMapping.ts`
- `lib/validation/portfolio.ts` — Zod schemas for the editable sections
- `components/portfolio/` — `PortfolioEditor` (data loading),
  `PortfolioEditForm` (the form), `ProfileFields`, `LinksFields`,
  `SkillsFields`, `ProjectsFields` (all `useFieldArray`-based),
  `UndocumentedSections`
- `components/ui/Textarea.tsx`

## 2. API endpoints consumed

`GET`/`PUT` on `PORTFOLIO_PATHS.me` (`/api/v1/portfolios/me`) —
**unconfirmed**, same flagged-guess treatment as `AUTH_PATHS`. A 404 on
GET is treated as "no portfolio yet" (start blank), not an error.

## 3. A real contract gap found while building this — not a missing feature

`PortfolioSchema.md` documents `profile`, `links`, `skills`, and
`projects` with a full example. It does **not** document the item shape
for `experience`, `education`, `certifications`, `achievements`,
`services`, `testimonials`, or `customSections` — the example just shows
empty arrays. Building forms for those would mean inventing field names
with no contract behind them, which is exactly what this project's own
rules say not to do ("do not invent backend endpoints, fields,
permissions or statuses").

So: those seven sections are editable nowhere in this frontend. They're
named honestly in the UI (`UndocumentedSections.tsx`) as not yet
definable, and preserved as-is on save via `formMapping.ts`'s `...base`
spread — nothing in them gets dropped or overwritten, they're just not
touched. **This needs a decision from whoever owns `PortfolioSchema.md`**
before Phase 2 can actually be called complete: define the item shape
for at least `experience` and `education` (the two ~everyone expects on
a portfolio), or explicitly confirm they're deferred past V1.

## 4. Other assumptions made, flagged in code

- **Save semantics**: single `PUT` (upsert), not
  `POST`-to-create-then-`PATCH`-to-update. Simplest assumption that
  satisfies "editor, section management" — revisit once B2 exists.
- **New project ids**: client-generates `project_<uuid>` for items added
  in the editor (`formMapping.ts#newProjectId`). Whether the backend
  accepts client-supplied ids or reassigns its own is unconfirmed.
- **No asset upload UI**: `profile.avatar` and `project.image` are
  `assetId` references in the schema, but no doc anywhere defines an
  upload endpoint. Rather than build a fake upload button, avatar/image
  are simply preserved from whatever was loaded and never editable here.
- **Skill `level` / link `type`** have example values (`"advanced"`,
  `"github"`) but no enum. Left as free-text inputs rather than guessing
  a fixed option list the server might reject.
- **Manual save only**, no autosave. The docs explicitly allow either
  ("autosave/manual-save UX as contract allows") — autosave adds real
  assumptions (debounce interval, conflict handling) on top of an
  already-unconfirmed contract, so it's deferred rather than guessed.

## 5. Environment variables required

Unchanged from Phase 1.

## 6. Tests/checks performed

- `npm run build` / `npm run lint` — clean.
- Live `next dev` smoke test on `/dashboard/portfolio`,
  `/dashboard/portfolio/editor`, and `/dashboard` — all render correctly
  (behind `RequireAuth`, so SSR shows the session-check state, same as
  every other dashboard route).
- Not verified: an actual save round trip, or that
  `applyServerFieldErrors`' `{ field, message }` assumption holds for
  whatever the real 422 response looks like.

## 7. Changes that require backend contract updates

- The `PortfolioSchema.md` gap in §3 above — this is the one that
  actually blocks calling Phase 2 done, not just a nice-to-have.
- Real OpenAPI for `GET`/`PUT` `/portfolios/me` — request/response
  shape, whether it's really a single-resource-per-user model, save
  semantics (upsert vs. create/update split).
- Confirm the client-generated project id assumption.
- An asset upload contract, whenever avatar/project images are wanted.
