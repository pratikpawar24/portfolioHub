import type { Metadata } from "next";
import { TemplateCatalogue } from "@/components/templates/TemplateCatalogue";

export const metadata: Metadata = { title: "Templates" };

export default function TemplatesPage() {
  return (
    <div className="flex flex-col gap-8">
      <div>
        <h1 className="text-2xl">Templates</h1>
        <p className="mt-2 text-[var(--color-ink-muted)]">
          Browse every approved template without visiting anyone&apos;s published portfolio.
        </p>
      </div>
      <TemplateCatalogue />
    </div>
  );
}
