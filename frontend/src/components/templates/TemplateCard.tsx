import Link from "next/link";
import type { TemplateSummary } from "@/lib/templates/types";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { ErrorState } from "@/components/states/ErrorState";
import { useApplyTemplate } from "./useApplyTemplate";

export function TemplateCard({ template }: { template: TemplateSummary }) {
  const { apply, applying, error } = useApplyTemplate();

  return (
    <div className="flex flex-col overflow-hidden rounded-[var(--radius)] border border-[var(--color-line)] bg-[var(--color-surface)]">
      <Link
        href={`/templates/${template.slug}`}
        className="block aspect-[4/3] bg-[var(--color-surface-sunken)]"
      >
        {template.currentVersion.previewImageUrl ? (
          // Preview host is unconfirmed (see PHASE_3_NOTES.md), so this
          // isn't configured through next/image's remote-pattern allowlist.
          // eslint-disable-next-line @next/next/no-img-element
          <img
            src={template.currentVersion.previewImageUrl}
            alt=""
            loading="lazy"
            className="h-full w-full object-cover"
          />
        ) : (
          <div className="flex h-full items-center justify-center text-sm text-[var(--color-ink-faint)]">
            No preview yet
          </div>
        )}
      </Link>
      <div className="flex flex-1 flex-col gap-3 p-4">
        <div>
          <Link
            href={`/templates/${template.slug}`}
            className="font-semibold hover:text-[var(--color-accent)]"
          >
            {template.name}
          </Link>
          <p className="text-sm text-[var(--color-ink-muted)]">
            {template.creator ? `by ${template.creator.displayName}` : "PortfolioHub first-party"}
          </p>
        </div>
        <div className="flex flex-wrap gap-2">
          <Badge tone="accent">{template.currentVersion.framework}</Badge>
          {template.category ? <Badge>{template.category}</Badge> : null}
          <Badge>
            schema {template.currentVersion.portfolioSchema.min}–
            {template.currentVersion.portfolioSchema.max}
          </Badge>
        </div>
        {error ? (
          <ErrorState kind={error.kind} message={error.body?.message} requestId={error.body?.requestId} />
        ) : null}
        <Button
          type="button"
          variant="primary"
          className="mt-auto"
          disabled={applying}
          onClick={() => apply(template.currentVersion.id)}
        >
          {applying ? "Applying…" : "Use template"}
        </Button>
      </div>
    </div>
  );
}
