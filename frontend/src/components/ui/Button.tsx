import Link from "next/link";
import type { ButtonHTMLAttributes, ReactNode } from "react";

type Variant = "primary" | "secondary" | "ghost";

const VARIANT_CLASSES: Record<Variant, string> = {
  primary:
    "bg-[var(--color-accent)] text-white hover:bg-[var(--color-accent-strong)]",
  secondary:
    "border border-[var(--color-line-strong)] bg-[var(--color-surface)] text-[var(--color-ink)] hover:bg-[var(--color-surface-sunken)]",
  ghost:
    "text-[var(--color-ink)] hover:bg-[var(--color-surface-sunken)]",
};

const BASE =
  "inline-flex items-center justify-center gap-2 rounded-[var(--radius)] px-4 py-2 text-sm font-medium transition-colors disabled:cursor-not-allowed disabled:opacity-50";

interface CommonProps {
  variant?: Variant;
  children: ReactNode;
  className?: string;
}

interface ButtonAsButton
  extends CommonProps,
    Omit<ButtonHTMLAttributes<HTMLButtonElement>, "className" | "children"> {
  href?: undefined;
}

interface ButtonAsLink extends CommonProps {
  href: string;
}

type ButtonProps = ButtonAsButton | ButtonAsLink;

export function Button({ variant = "primary", children, className = "", ...rest }: ButtonProps) {
  const classes = `${BASE} ${VARIANT_CLASSES[variant]} ${className}`;

  if ("href" in rest && rest.href) {
    return (
      <Link href={rest.href} className={classes}>
        {children}
      </Link>
    );
  }

  const buttonProps = rest as Omit<ButtonHTMLAttributes<HTMLButtonElement>, "className" | "children">;
  return (
    <button className={classes} {...buttonProps}>
      {children}
    </button>
  );
}
