const UNDOCUMENTED_SECTIONS = [
  { key: "experience", label: "Experience" },
  { key: "education", label: "Education" },
  { key: "certifications", label: "Certifications" },
  { key: "achievements", label: "Achievements" },
  { key: "services", label: "Services" },
  { key: "testimonials", label: "Testimonials" },
  { key: "customSections", label: "Custom sections" },
] as const;

/**
 * PortfolioSchema.md shows these as empty arrays with no example item —
 * there's no field shape to build a form against. Editing them here
 * would mean inventing schema, which is exactly what this frontend is
 * supposed to not do. See PHASE_2_NOTES.md.
 */
export function UndocumentedSections() {
  return (
    <section aria-labelledby="undocumented-heading" className="flex flex-col gap-4">
      <h2 id="undocumented-heading" className="text-lg font-semibold">
        Not editable yet
      </h2>
      <div className="rounded-[var(--radius)] border border-dashed border-[var(--color-line-strong)] bg-[var(--color-surface)] p-4">
        <p className="text-sm text-[var(--color-ink-muted)]">
          {UNDOCUMENTED_SECTIONS.map((s) => s.label).join(", ")} aren&apos;t editable here yet —
          the canonical schema doesn&apos;t define what fields they hold, so building a form for
          them now would mean guessing. Anything already in these sections is preserved when you
          save.
        </p>
      </div>
    </section>
  );
}
