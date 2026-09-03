import type { ReactNode } from "react";

interface EmptyStateProps {
  heading: string;
  description?: string;
  action?: ReactNode;
}

export function EmptyState({ heading, description, action }: EmptyStateProps) {
  return (
    <div className="flex flex-col items-start gap-3 rounded-[var(--radius)] border border-dashed border-[var(--color-line-strong)] bg-[var(--color-surface)] px-6 py-10">
      <h3 className="text-lg font-semibold">{heading}</h3>
      {description ? (
        <p className="max-w-prose text-sm text-[var(--color-ink-muted)]">{description}</p>
      ) : null}
      {action}
    </div>
  );
}
