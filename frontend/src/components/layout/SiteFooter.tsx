export function SiteFooter() {
  return (
    <footer className="border-t border-[var(--color-line)]">
      <div className="mx-auto flex max-w-6xl flex-col gap-1 px-6 py-8 text-sm text-[var(--color-ink-muted)] sm:flex-row sm:items-center sm:justify-between">
        <p>© {new Date().getFullYear()} PortfolioHub</p>
        <p>Your content, any compatible template.</p>
      </div>
    </footer>
  );
}
