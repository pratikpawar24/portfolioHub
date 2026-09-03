interface LoadingStateProps {
  label?: string;
  /** Render as a small inline row instead of filling the container */
  compact?: boolean;
}

export function LoadingState({ label = "Loading…", compact = false }: LoadingStateProps) {
  return (
    <div
      role="status"
      aria-live="polite"
      className={
        compact
          ? "inline-flex items-center gap-2 text-sm text-[var(--color-ink-muted)]"
          : "flex flex-col items-center justify-center gap-3 py-16 text-[var(--color-ink-muted)]"
      }
    >
      <span
        aria-hidden="true"
        className="h-4 w-4 animate-spin rounded-full border-2 border-[var(--color-line-strong)] border-t-[var(--color-accent)]"
      />
      <span className={compact ? "" : "text-sm"}>{label}</span>
    </div>
  );
}
