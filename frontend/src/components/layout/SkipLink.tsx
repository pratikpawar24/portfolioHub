export function SkipLink() {
  return (
    <a
      href="#main"
      className="sr-only focus:not-sr-only focus:absolute focus:left-4 focus:top-4 focus:z-50 focus:rounded-[var(--radius)] focus:bg-[var(--color-ink)] focus:px-4 focus:py-2 focus:text-sm focus:font-medium focus:text-white"
    >
      Skip to main content
    </a>
  );
}
