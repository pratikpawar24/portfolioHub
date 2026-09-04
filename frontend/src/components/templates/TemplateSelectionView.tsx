"use client";

import { useEffect, useState } from "react";
import { listTemplates } from "@/lib/templates/api";
import type { TemplateSummary } from "@/lib/templates/types";
import { resolvePortfolio } from "@/components/portfolio/PortfolioEditor";
import { ApiError } from "@/lib/api/client";
import { LoadingState } from "@/components/states/LoadingState";
import { ErrorState } from "@/components/states/ErrorState";
import { EmptyState } from "@/components/states/EmptyState";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { useApplyTemplate } from "./useApplyTemplate";

type LoadResult =
  | { status: "loaded"; templates: TemplateSummary[]; activeTemplateVersionId: string | null }
  | { status: "error"; error: ApiError };

async function resolveSelectionData(): Promise<LoadResult> {
  const [templatesResult, portfolioResult] = await Promise.allSettled([
    listTemplates(),
    resolvePortfolio(),
  ]);

  if (templatesResult.status === "rejected") {
    const err = templatesResult.reason;
    if (err instanceof ApiError) return { status: "error", error: err };
    throw err;
  }

  const activeTemplateVersionId =
    portfolioResult.status === "fulfilled" && portfolioResult.value.status === "loaded"
      ? portfolioResult.value.resource.activeTemplateVersionId
      : null;

  return { status: "loaded", templates: templatesResult.value, activeTemplateVersionId };
}

function SelectionCard({
  template,
  isActive,
}: {
  template: TemplateSummary;
  isActive: boolean;
}) {
  const { apply, applying, error } = useApplyTemplate();

  return (
    <div
      className={`flex flex-col gap-3 rounded-[var(--radius)] border p-4 ${
        isActive ? "border-[var(--color-accent)]" : "border-[var(--color-line)]"
      } bg-[var(--color-surface)]`}
    >
      <div>
        <p className="font-semibold">{template.name}</p>
        <p className="text-sm text-[var(--color-ink-muted)]">
          {template.creator ? `by ${template.creator.displayName}` : "PortfolioHub first-party"}
        </p>
      </div>
      <div className="flex flex-wrap gap-2">
        <Badge tone="accent">{template.currentVersion.framework}</Badge>
        {isActive ? <Badge tone="live">Active</Badge> : null}
      </div>
      {error ? (
        <ErrorState kind={error.kind} message={error.body?.message} requestId={error.body?.requestId} />
      ) : null}
      <Button
        type="button"
        variant={isActive ? "secondary" : "primary"}
        disabled={applying || isActive}
        onClick={() => apply(template.currentVersion.id)}
      >
        {isActive ? "Currently applied" : applying ? "Applying…" : "Use this template"}
      </Button>
    </div>
  );
}

export function TemplateSelectionView() {
  const [state, setState] = useState<LoadResult | { status: "loading" }>({ status: "loading" });

  useEffect(() => {
    let cancelled = false;
    resolveSelectionData().then((result) => {
      if (!cancelled) setState(result);
    });
    return () => {
      cancelled = true;
    };
  }, []);

  if (state.status === "loading") {
    return <LoadingState label="Loading templates…" />;
  }

  if (state.status === "error") {
    return (
      <ErrorState kind={state.error.kind} message={state.error.body?.message} requestId={state.error.body?.requestId} />
    );
  }

  if (state.templates.length === 0) {
    return (
      <EmptyState
        heading="No templates available yet"
        description="Approved first-party templates will appear here."
      />
    );
  }

  return (
    <div className="grid gap-4 sm:grid-cols-2">
      {state.templates.map((template) => (
        <SelectionCard
          key={template.id}
          template={template}
          isActive={template.currentVersion.id === state.activeTemplateVersionId}
        />
      ))}
    </div>
  );
}
