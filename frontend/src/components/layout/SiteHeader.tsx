import Link from "next/link";
import { Button } from "@/components/ui/Button";

export function SiteHeader() {
  return (
    <header className="border-b border-[var(--color-line)]">
      <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-4">
        <Link href="/" className="font-[var(--font-display)] text-lg font-semibold">
          PortfolioHub
        </Link>
        <nav aria-label="Primary" className="flex items-center gap-6">
          <Link href="/templates" className="text-sm text-[var(--color-ink-muted)] hover:text-[var(--color-ink)]">
            Templates
          </Link>
          <Link href="/login" className="text-sm text-[var(--color-ink-muted)] hover:text-[var(--color-ink)]">
            Log in
          </Link>
          <Button href="/register" variant="primary">
            Create your portfolio
          </Button>
        </nav>
      </div>
    </header>
  );
}
