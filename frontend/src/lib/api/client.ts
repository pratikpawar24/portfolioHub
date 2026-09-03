import { API_BASE_URL, API_VERSION_PREFIX, HEALTH_PATH } from "./config";
import type { ApiErrorBody, ApiErrorKind } from "./types";

export class ApiError extends Error {
  readonly kind: ApiErrorKind;
  readonly status: number | null;
  readonly body: ApiErrorBody | null;

  constructor(kind: ApiErrorKind, status: number | null, body: ApiErrorBody | null, message: string) {
    super(message);
    this.name = "ApiError";
    this.kind = kind;
    this.status = status;
    this.body = body;
  }
}

function classify(status: number): ApiErrorKind {
  switch (status) {
    case 401:
      return "unauthorized";
    case 403:
      return "forbidden";
    case 404:
      return "not_found";
    case 409:
      return "conflict";
    case 422:
      return "validation";
    case 429:
      return "rate_limited";
    default:
      return "server";
  }
}

export interface ApiFetchOptions extends RequestInit {
  /** Path relative to API_VERSION_PREFIX, e.g. "/portfolios/me" */
  path: string;
}

/**
 * Thin typed wrapper around fetch. Every non-2xx response is turned into
 * a classified ApiError with the parsed error envelope attached — callers
 * decide the UI treatment (see components/states), this layer never
 * decides it for them and never synthesizes a fake success value.
 */
export async function apiFetch<T>({ path, headers, ...init }: ApiFetchOptions): Promise<T> {
  const url = `${API_BASE_URL}${API_VERSION_PREFIX}${path}`;

  let response: Response;
  try {
    response = await fetch(url, {
      ...init,
      headers: {
        Accept: "application/json",
        ...(init.body ? { "Content-Type": "application/json" } : {}),
        ...headers,
      },
      credentials: "include",
    });
  } catch {
    throw new ApiError("network", null, null, "Could not reach the PortfolioHub API.");
  }

  if (!response.ok) {
    let body: ApiErrorBody | null = null;
    try {
      body = (await response.json()) as ApiErrorBody;
    } catch {
      // backend didn't return a parseable error envelope — proceed with body = null
    }
    const kind = classify(response.status);
    throw new ApiError(kind, response.status, body, body?.message ?? response.statusText);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return (await response.json()) as T;
}

export interface HealthStatus {
  reachable: boolean;
  raw?: unknown;
}

/**
 * Used for the Phase 0 sync gate: "Frontend can reach the backend
 * health/API contract and display consistent error responses." Not
 * routed through apiFetch/API_VERSION_PREFIX since Actuator health is
 * conventionally unversioned.
 */
export async function getHealth(): Promise<HealthStatus> {
  try {
    const res = await fetch(`${API_BASE_URL}${HEALTH_PATH}`, {
      headers: { Accept: "application/json" },
    });
    if (!res.ok) return { reachable: false };
    return { reachable: true, raw: await res.json().catch(() => undefined) };
  } catch {
    return { reachable: false };
  }
}
