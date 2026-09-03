import type { ApiErrorKind } from "@/lib/api/types";

interface ErrorStateProps {
  kind: ApiErrorKind;
  /** Backend-provided message, when there is one — shown alongside the default explanation. */
  message?: string;
  requestId?: string | null;
  onRetry?: () => void;
}

const COPY: Record<ApiErrorKind, { heading: string; description: string; retryable: boolean }> = {
  unauthorized: {
    heading: "You're signed out",
    description: "Sign in again to continue.",
    retryable: false,
  },
  forbidden: {
    heading: "You don't have access to this",
    description: "This is limited to accounts with permission for it.",
    retryable: false,
  },
  not_found: {
    heading: "Not found",
    description: "This may have been moved, unpublished, or never existed.",
    retryable: false,
  },
  conflict: {
    heading: "That change conflicts with something else",
    description: "Refresh and try again with the latest version.",
    retryable: true,
  },
  validation: {
    heading: "There's a problem with the submitted data",
    description: "Check the highlighted fields and try again.",
    retryable: false,
  },
  rate_limited: {
    heading: "Too many requests",
    description: "Wait a moment before trying again.",
    retryable: true,
  },
  server: {
    heading: "Something went wrong on our end",
    description: "This is retryable — it isn't something you did.",
    retryable: true,
  },
  network: {
    heading: "Can't reach PortfolioHub",
    description: "Check your connection and try again.",
    retryable: true,
  },
};

export function ErrorState({ kind, message, requestId, onRetry }: ErrorStateProps) {
  const copy = COPY[kind];
  return (
    <div
      role="alert"
      className="flex flex-col items-start gap-3 rounded-[var(--radius)] border border-[var(--color-signal-danger)]/30 bg-[var(--color-signal-danger-soft)] px-6 py-6"
    >
      <h3 className="text-base font-semibold text-[var(--color-ink)]">{copy.heading}</h3>
      <p className="text-sm text-[var(--color-ink-muted)]">{message ?? copy.description}</p>
      {copy.retryable && onRetry ? (
        <button
          type="button"
          onClick={onRetry}
          className="rounded-[var(--radius)] border border-[var(--color-line-strong)] bg-[var(--color-surface)] px-3 py-1.5 text-sm font-medium hover:bg-[var(--color-surface-sunken)]"
        >
          Try again
        </button>
      ) : null}
      {requestId ? (
        <p className="font-mono text-xs text-[var(--color-ink-faint)]">Reference: {requestId}</p>
      ) : null}
    </div>
  );
}
