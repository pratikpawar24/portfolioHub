import type { ReactNode } from "react";

interface BadgeProps {
  children: ReactNode;
  tone?: "neutral" | "accent" | "live" | "warn" | "danger";
}

const TONE_CLASSES: Record<NonNullable<BadgeProps["tone"]>, string> = {
  neutral: "bg-[var(--color-surface-sunken)] text-[var(--color-ink-muted)]",
  accent: "bg-[var(--color-accent-soft)] text-[var(--color-accent-strong)]",
  live: "bg-[var(--color-signal-live-soft)] text-[var(--color-signal-live)]",
  warn: "bg-[var(--color-signal-warn-soft)] text-[var(--color-signal-warn)]",
  danger: "bg-[var(--color-signal-danger-soft)] text-[var(--color-signal-danger)]",
};

export function Badge({ children, tone = "neutral" }: BadgeProps) {
  return (
    <span
      className={`inline-flex items-center rounded-[var(--radius)] px-2 py-0.5 font-mono text-xs ${TONE_CLASSES[tone]}`}
    >
      {children}
    </span>
  );
}
