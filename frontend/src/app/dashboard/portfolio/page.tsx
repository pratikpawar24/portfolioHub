"use client";

import { useEffect, useState } from "react";
import { resolvePortfolio, type LoadResult } from "@/components/portfolio/PortfolioEditor";
import { LoadingState } from "@/components/states/LoadingState";
import { ErrorState } from "@/components/states/ErrorState";
import { EmptyState } from "@/components/states/EmptyState";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";

export default function PortfolioOverviewPage() {
  const [state, setState] = useState<LoadResult | { status: "loading" }>({ status: "loading" });

  useEffect(() => {
    let cancelled = false;
    resolvePortfolio().then((result) => {
      if (!cancelled) setState(result);
    });
    return () => {
      cancelled = true;
    };
  }, []);

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl">Portfolio</h1>

      {state.status === "loading" ? <LoadingState label="Loading your portfolio…" /> : null}

      {state.status === "error" ? (
        <ErrorState
          kind={state.error.kind}
          message={state.error.body?.message}
          requestId={state.error.body?.requestId}
        />
      ) : null}

      {state.status === "loaded" && !state.resource.content.profile.displayName ? (
        <EmptyState
          heading="Nothing written yet"
          description="Add your profile, links, skills and projects to get started."
          action={
            <Button href="/dashboard/portfolio/editor" variant="primary">
              Open editor
            </Button>
          }
        />
      ) : null}

      {state.status === "loaded" && state.resource.content.profile.displayName ? (
        <div className="flex flex-col gap-4 rounded-[var(--radius)] border border-[var(--color-line)] bg-[var(--color-surface)] p-5">
          <div>
            <p className="text-lg font-semibold">{state.resource.content.profile.displayName}</p>
            {state.resource.content.profile.headline ? (
              <p className="text-sm text-[var(--color-ink-muted)]">
                {state.resource.content.profile.headline}
              </p>
            ) : null}
          </div>
          <div className="flex flex-wrap gap-2">
            <Badge>{state.resource.content.skills.length} skills</Badge>
            <Badge>{state.resource.content.projects.length} projects</Badge>
            <Badge>{state.resource.content.links.length} links</Badge>
            <Badge tone={state.resource.activeTemplateVersionId ? "live" : "warn"}>
              {state.resource.activeTemplateVersionId ? "template applied" : "no template"}
            </Badge>
          </div>
          <div className="flex gap-3">
            <Button href="/dashboard/portfolio/editor" variant="secondary">
              Edit portfolio
            </Button>
            <Button href="/dashboard/portfolio/templates" variant="secondary">
              {state.resource.activeTemplateVersionId ? "Change template" : "Choose a template"}
            </Button>
          </div>
        </div>
      ) : null}
    </div>
  );
}
