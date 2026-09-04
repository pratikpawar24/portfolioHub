import { forwardRef, type TextareaHTMLAttributes } from "react";

interface TextareaFieldProps extends TextareaHTMLAttributes<HTMLTextAreaElement> {
  label: string;
  error?: string;
  hint?: string;
}

export const TextareaField = forwardRef<HTMLTextAreaElement, TextareaFieldProps>(
  function TextareaField({ label, error, hint, id, className = "", ...rest }, ref) {
    const errorId = error ? `${id}-error` : undefined;
    const hintId = hint ? `${id}-hint` : undefined;

    return (
      <div className="flex flex-col gap-1.5">
        <label htmlFor={id} className="text-sm font-medium">
          {label}
        </label>
        <textarea
          ref={ref}
          id={id}
          aria-invalid={error ? true : undefined}
          aria-describedby={[errorId, hintId].filter(Boolean).join(" ") || undefined}
          rows={4}
          className={`rounded-[var(--radius)] border bg-[var(--color-surface)] px-3 py-2 text-sm ${
            error ? "border-[var(--color-signal-danger)]" : "border-[var(--color-line-strong)]"
          } ${className}`}
          {...rest}
        />
        {hint ? (
          <p id={hintId} className="text-xs text-[var(--color-ink-faint)]">
            {hint}
          </p>
        ) : null}
        {error ? (
          <p id={errorId} role="alert" className="text-xs text-[var(--color-signal-danger)]">
            {error}
          </p>
        ) : null}
      </div>
    );
  },
);
