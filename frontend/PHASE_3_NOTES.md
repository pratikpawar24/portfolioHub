# Frontend Phase 3 — Template Contract + Registry

Per `ParallelPhasePlan.md`, Phase 3 frontend scope is: global template
catalogue, template detail, selection/application. Sync gate: **"A user
can browse the central catalogue and apply a compatible template
without duplicating portfolio data."** Not met — no backend to browse or
apply against (`Memory.md` still shows B0 only). Same standard as every
phase so far: real logic against a clearly-flagged best-guess contract,
verified by build/lint/live-render.

## 0. A Phase 2 correction, made before starting Phase 3

Cross-referencing `DatabaseDesign.md` §2 (not fully done during Phase 2)
surfaced a real modeling error: `portfolios` is its own row — `id`,
`slug`, `title`, `status`, `active_template_version_id`,
`current_draft_revision_id`, `published_revision_id` — separate from
`portfolio_revisions.content` (the JSONB matching `PortfolioDocument`).
Phase 2 treated `GET /portfolios/me` as if it returned the content
directly. That can't be right once you need `activeTemplateVersionId`
for template selection, and it isn't part of the content schema.

Fixed by splitting the type: `PortfolioResource` (entity, with `content`
nested) replaces the old direct-document assumption everywhere —
`lib/portfolio/types.ts`, `api.ts`, `PortfolioEditor.tsx`,
`PortfolioEditForm.tsx`, and both dashboard pages that read portfolio
state. Also split the save endpoint: `PUT /portfolios/me/content` for
edits, `PUT /portfolios/me/active-template` for template changes, so
saving a content edit doesn't require re-sending template linkage.
**Still a guess** — just a better-grounded one than before.

Also fixed in passing: `dashboard/page.tsx` had "Ready for Phase 1"
hardcoded in user-facing copy, which had already gone stale by Phase 2
and would keep doing that every phase. Removed the phase number from
the sentence entirely.

## 1. Implemented routes/components

- `/templates` — real catalogue (`TemplateCatalogue`, `TemplateCard`),
  replacing the Phase 0 stub
- `/templates/[slug]` — real detail view (`TemplateDetailView`)
- `/dashboard/portfolio/templates` — real selection view
  (`TemplateSelectionView`), aware of which template (if any) is
  currently active
- `components/templates/useApplyTemplate.ts` — shared "Use template"
  action: applies directly if signed in, sends anonymous visitors to
  `/register` first (can't apply a template without a portfolio to
  apply it to)

New code: `lib/templates/types.ts`, `lib/templates/api.ts`

## 2. API endpoints consumed

All unconfirmed:
- `GET /api/v1/templates` → `TemplateSummary[]`
- `GET /api/v1/templates/{slug}` → `TemplateDetail`
- `PUT /api/v1/portfolios/me/active-template` (moved from Phase 2's
  single `/portfolios/me`, see §0)

## 3. Registry response shape — derived, not confirmed

No doc defines the catalogue/registry API's actual JSON shape. Modeled
in `lib/templates/types.ts` as `Template` (stable identity: slug, name,
category, license, creator) + `currentVersion` (framework, capabilities,
schema range, preview) as two nested pieces rather than one flat object
— because `DatabaseDesign.md` splits `templates` from `template_versions`
that way, and framework/capabilities/schema-compatibility genuinely
belong to a *version*, not the template as a whole. Better-grounded than
Phase 2's health-check-style guesses, but still a shape guess: field
names, casing, and nesting are mine, not the backend's.

Specific things assumed:
- `creator: TemplateCreator | null` — null for first-party templates,
  matching `creator_user_id` being nullable in the DB and
  FrontendDevelopmentPrompt.md's "creator attribution **where
  available**."
- `category` is a single string (matches the DB column), not a tags
  array — satisfies the UI spec's "tags/category" as one field.
  `previewImageUrl` assumed resolved server-side from
  `template_versions.preview_reference` into a usable URL — not
  configured through `next/image`'s remote-pattern allowlist since the
  host is unknown; using plain `<img loading="lazy">` instead
  (flagged inline in `TemplateCard.tsx`/`TemplateDetailView.tsx`).
- `currentVersion.id` is assumed to be the actual `template_versions.id`
  UUID needed for `active-template` — the one field this frontend can't
  function without guessing correctly.

## 4. Environment variables required

Unchanged.

## 5. Tests/checks performed

- `npm run build` / `npm run lint` — clean.
- Live `next dev` smoke test: `/templates`, `/templates/[slug]`,
  `/dashboard/portfolio/templates`, `/dashboard/portfolio/editor` all
  render their loading state correctly, no runtime errors.
- Not verified: an actual catalogue fetch, template application, or
  that `currentVersion.id` really is what `active-template` expects.

## 6. Changes that require backend contract updates

- Real OpenAPI for `GET /templates`, `GET /templates/{slug}`, and
  `PUT /portfolios/me/active-template` — this phase's shape guesses are
  the biggest ones yet (two-object model, nullable creator, id semantics)
  and most need confirming before they can be trusted.
- Confirm the `PortfolioResource` / content-vs-entity split from §0 —
  this one blocks Phase 2 *and* Phase 3 both being called done.
- Confirm how `preview_reference` resolves to a fetchable image URL.
