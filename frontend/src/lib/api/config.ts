/**
 * All backend calls go through NEXT_PUBLIC_API_BASE_URL. Never hardcode a
 * host here — see BackendFrontendContract.md: the backend's OpenAPI
 * definition is authoritative, this file only points at it.
 */
export const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

export const API_VERSION_PREFIX = "/api/v1";

/**
 * NOT YET CONFIRMED AGAINST THE BACKEND CONTRACT.
 * BackendFrontendContract.md / ParallelPhasePlan.md Phase 0 lists
 * "Actuator/health" as a backend deliverable, which for Spring Boot
 * defaults to this path — but the exact path is backend-owned. Update
 * this the moment the backend chat publishes its OpenAPI/actuator config,
 * per the "do not invent backend endpoints" rule.
 */
export const HEALTH_PATH = "/actuator/health";

/**
 * NOT YET CONFIRMED AGAINST THE BACKEND CONTRACT.
 * PRD.md §7.1 says "Session/token management" and Requirements.md §5
 * requires explicit CSRF/CORS policy — together that reads as cookie-
 * session auth, not bearer tokens, which is why apiFetch always sends
 * credentials: "include". But no doc states the actual mechanism,
 * cookie names, or a CSRF header requirement. These paths follow a
 * conventional Spring Security REST layout as a starting guess — update
 * every value here (and check whether a CSRF token header needs adding
 * to apiFetch) the moment the backend chat publishes real OpenAPI auth
 * endpoints.
 */
export const AUTH_PATHS = {
  register: "/auth/register",
  login: "/auth/login",
  logout: "/auth/logout",
  me: "/auth/me",
} as const;

/**
 * NOT CONFIRMED. Revised from the original Phase 2 guess after cross-
 * referencing DatabaseDesign.md §2: `portfolios` (entity: id, slug,
 * title, status, active_template_version_id, revision pointers) is a
 * separate row from `portfolio_revisions.content` (the JSONB matching
 * PortfolioDocument). `me` is assumed to return the entity with content
 * embedded (PortfolioResource) rather than content alone — a common
 * enough REST pattern, but still a guess. `content` and `activeTemplate`
 * are split out so saving an edit doesn't require re-sending template
 * linkage, and vice versa.
 */
export const PORTFOLIO_PATHS = {
  me: "/portfolios/me",
  content: "/portfolios/me/content",
  activeTemplate: "/portfolios/me/active-template",
} as const;

/**
 * NOT CONFIRMED. Derived from DatabaseDesign.md's `templates` /
 * `template_versions` tables plus the catalogue/detail fields
 * FrontendDevelopmentPrompt.md §7 asks for (preview, name, creator,
 * framework, tags/category, compatible schema, license, version). No
 * doc defines the actual registry API response shape — see
 * lib/templates/types.ts and PHASE_3_NOTES.md.
 */
export const TEMPLATE_PATHS = {
  list: "/templates",
  detail: (slug: string) => `/templates/${slug}`,
} as const;
