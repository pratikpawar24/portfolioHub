import Link from "next/link";
import { SkipLink } from "@/components/layout/SkipLink";

// Deliberately no header/nav here — the published portfolio is the
// focus, not PortfolioHub chrome (FrontendDevelopmentPrompt.md §7,
// "Public Portfolio"). This must render without any dashboard session.
export default function PublicPortfolioLayout({ children }: { children: React.ReactNode }) {
  return (
    <>
      <SkipLink />
      <main id="main">{children}</main>
      <footer className="border-t border-[var(--color-line)] px-6 py-4 text-center text-xs text-[var(--color-ink-faint)]">
        Published with{" "}
        <Link href="/" className="underline hover:text-[var(--color-ink-muted)]">
          PortfolioHub
        </Link>
      </footer>
    </>
  );
}
