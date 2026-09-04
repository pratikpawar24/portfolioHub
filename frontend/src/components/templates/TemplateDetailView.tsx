"use client";

import { useEffect, useState } from "react";
import { getTemplateDetail } from "@/lib/templates/api";
import type { TemplateDetail } from "@/lib/templates/types";
import { ApiError } from "@/lib/api/client";
import { LoadingState } from "@/components/states/LoadingState";
import { ErrorState } from "@/components/states/ErrorState";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { useApplyTemplate } from "./useApplyTemplate";

type LoadResult =
  | { status: "loaded"; template: TemplateDetail }
  | { status: "error"; error: ApiError };

export function TemplateDetailView({ slug }: { slug: string }) {
  const [state, setState] = useState<LoadResult | { status: "loading" }>({ status: "loading" });
  const { apply, applying, error: applyError } = useApplyTemplate();

  useEffect(() => {
    let cancelled = false;
    getTemplateDetail(slug).then(
      (template) => {
        if (!cancelled) setState({ status: "loaded", template });
      },
      (err) => {
        if (cancelled) return;
        if (err instanceof ApiError) setState({ status: "error", error: err });
        else throw err;
      },
    );
    return () => {
      cancelled = true;
    };
  }, [slug]);

  if (state.status === "loading") {
    return <LoadingState label="Loading template…" />;
  }

  if (state.status === "error") {
    return (
      <ErrorState
        kind={state.error.kind}
        message={state.error.body?.message}
        requestId={state.error.body?.requestId}
      />
    );
  }

  const { template } = state;
  const v = template.currentVersion;

  return (
    <div className="flex flex-col gap-8">
      <div className="aspect-[16/9] overflow-hidden rounded-[var(--radius)] bg-[var(--color-surface-sunken)]">
        {v.previewImageUrl ? (
          // eslint-disable-next-line @next/next/no-img-element
          <img src={v.previewImageUrl} alt="" className="h-full w-full object-cover" />
        ) : (
          <div className="flex h-full items-center justify-center text-sm text-[var(--color-ink-faint)]">
            No preview yet
          </div>
        )}
      </div>

      <div className="flex flex-col gap-3">
        <h1 className="text-2xl">{template.name}</h1>
        <p className="text-sm text-[var(--color-ink-muted)]">
          {template.creator ? `by ${template.creator.displayName}` : "PortfolioHub first-party"}
        </p>
        {template.description ? (
          <p className="max-w-prose text-[var(--color-ink-muted)]">{template.description}</p>
        ) : null}
      </div>

      <dl className="grid max-w-md grid-cols-2 gap-x-6 gap-y-3 text-sm">
        <dt className="text-[var(--color-ink-muted)]">Framework</dt>
        <dd className="font-mono">{v.framework}</dd>
        <dt className="text-[var(--color-ink-muted)]">Version</dt>
        <dd className="font-mono">{v.version}</dd>
        <dt className="text-[var(--color-ink-muted)]">License</dt>
        <dd>{template.license}</dd>
        <dt className="text-[var(--color-ink-muted)]">Compatible schema</dt>
        <dd className="font-mono">
          {v.portfolioSchema.min}–{v.portfolioSchema.max}
        </dd>
      </dl>

      <div className="flex flex-wrap gap-2">
        {v.capabilities.darkMode ? <Badge tone="accent">Dark mode</Badge> : null}
        {v.capabilities.customFonts ? <Badge tone="accent">Custom fonts</Badge> : null}
      </div>

      {applyError ? (
        <ErrorState
          kind={applyError.kind}
          message={applyError.body?.message}
          requestId={applyError.body?.requestId}
        />
      ) : null}

      <Button type="button" disabled={applying} onClick={() => apply(v.id)} className="self-start">
        {applying ? "Applying…" : "Use template"}
      </Button>
    </div>
  );
}
