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
 * NOT CONFIRMED. PortfolioSchema.md defines the document shape but no
 * doc defines the actual REST surface — no path, no verb split (single
 * upsert vs POST-to-create + PATCH-to-update), no confirmation that "my
 * portfolio" is even a single-resource-per-user model. GET+PUT on one
 * resource is the simplest assumption that satisfies Phase 2's frontend
 * scope ("editor, section management") without guessing more than
 * necessary. Revisit the moment backend Phase 2 (B2) publishes OpenAPI.
 */
export const PORTFOLIO_PATHS = {
  me: "/portfolios/me",
} as const;
