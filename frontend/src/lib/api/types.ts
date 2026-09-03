/**
 * Mirrors the "Minimum Error Contract" in BackendFrontendContract.md.
 * Exact fields/enums are backend-owned via OpenAPI — this is the shared
 * shape only, not a source of truth for business fields.
 */
export interface ApiErrorBody {
  code: string;
  message: string;
  requestId: string;
  details: unknown[];
}

/**
 * The distinct HTTP conditions this frontend must handle per
 * FrontendDevelopmentPrompt.md §10. Kept as a union (not just a number)
 * so UI code can switch on meaning, not memorize status codes.
 */
export type ApiErrorKind =
  | "unauthorized" // 401
  | "forbidden" // 403
  | "not_found" // 404
  | "conflict" // 409
  | "validation" // 422
  | "rate_limited" // 429
  | "server" // 5xx
  | "network"; // fetch itself failed — no response at all

/**
 * Deliberately no success-envelope type here (e.g. `{ data: T }`).
 * BackendFrontendContract.md defines an error shape but not a success
 * shape — inventing one would be exactly the "invent backend behavior"
 * mistake the contract warns against. apiFetch<T> below returns the
 * parsed JSON body as-is; add real response types per-endpoint once the
 * OpenAPI contract exists.
 */
