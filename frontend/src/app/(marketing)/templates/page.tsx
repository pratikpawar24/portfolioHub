import type { Metadata } from "next";
import { EmptyState } from "@/components/states/EmptyState";

export const metadata: Metadata = { title: "Templates" };

// Template catalogue wiring (search/filter, real cards) lands with the
// template contract/registry backend work — see ParallelPhasePlan.md
// Phase 3. Rendering hardcoded template cards here would violate
// FrontendDevelopmentPrompt.md §10 ("Never hard-code template lists"),
// so this stays an honest empty state until that API exists.
export default function TemplatesPage() {
  return (
    <div className="flex flex-col gap-8">
      <div>
        <h1 className="text-2xl">Templates</h1>
        <p className="mt-2 text-[var(--color-ink-muted)]">
          Browse every approved template without visiting anyone&apos;s published portfolio.
        </p>
      </div>
      <EmptyState
        heading="No templates available yet"
        description="We're still connecting the template catalogue. Check back soon."
      />
    </div>
  );
}
