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

      {state.status === "loaded" && !state.doc.profile.displayName ? (
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

      {state.status === "loaded" && state.doc.profile.displayName ? (
        <div className="flex flex-col gap-4 rounded-[var(--radius)] border border-[var(--color-line)] bg-[var(--color-surface)] p-5">
          <div>
            <p className="text-lg font-semibold">{state.doc.profile.displayName}</p>
            {state.doc.profile.headline ? (
              <p className="text-sm text-[var(--color-ink-muted)]">{state.doc.profile.headline}</p>
            ) : null}
          </div>
          <div className="flex flex-wrap gap-2">
            <Badge>{state.doc.skills.length} skills</Badge>
            <Badge>{state.doc.projects.length} projects</Badge>
            <Badge>{state.doc.links.length} links</Badge>
          </div>
          <Button href="/dashboard/portfolio/editor" variant="secondary" className="self-start">
            Edit portfolio
          </Button>
        </div>
      ) : null}
    </div>
  );
}
