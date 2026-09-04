"use client";

import { useEffect, useState } from "react";
import { getHealth, type HealthStatus } from "@/lib/api/client";
import { resolvePortfolio, type LoadResult } from "@/components/portfolio/PortfolioEditor";
import { LoadingState } from "@/components/states/LoadingState";
import { EmptyState } from "@/components/states/EmptyState";
import { ErrorState } from "@/components/states/ErrorState";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";

function BackendStatusCard() {
  const [health, setHealth] = useState<HealthStatus | "loading">("loading");

  async function check() {
    setHealth("loading");
    setHealth(await getHealth());
  }

  useEffect(() => {
    let cancelled = false;
    getHealth().then((result) => {
      if (!cancelled) setHealth(result);
    });
    return () => {
      cancelled = true;
    };
  }, []);

  return (
    <div className="rounded-[var(--radius)] border border-[var(--color-line)] bg-[var(--color-surface)] p-5">
      <div className="flex items-center justify-between">
        <h2 className="text-sm font-semibold text-[var(--color-ink-muted)]">Backend connection</h2>
        {health !== "loading" && (
          <Badge tone={health.reachable ? "live" : "danger"}>
            {health.reachable ? "reachable" : "unreachable"}
          </Badge>
        )}
      </div>
      <div className="mt-3">
        {health === "loading" ? (
          <LoadingState compact label="Checking API…" />
        ) : health.reachable ? (
          <p className="text-sm text-[var(--color-ink-muted)]">
            The API responded to a health check.
          </p>
        ) : (
          <div className="flex flex-col items-start gap-2">
            <p className="text-sm text-[var(--color-ink-muted)]">
              No response from the API. Expected until the backend is running locally, or if{" "}
              <code>NEXT_PUBLIC_API_BASE_URL</code> isn&apos;t set.
            </p>
            <button
              type="button"
              onClick={check}
              className="text-sm font-medium text-[var(--color-accent)] hover:text-[var(--color-accent-strong)]"
            >
              Check again
            </button>
          </div>
        )}
      </div>
    </div>
  );
}

function PortfolioStatusCard() {
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

  if (state.status === "loading") {
    return (
      <div className="rounded-[var(--radius)] border border-[var(--color-line)] bg-[var(--color-surface)] p-5">
        <LoadingState compact label="Loading your portfolio…" />
      </div>
    );
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

  if (!state.resource.content.profile.displayName) {
    return (
      <EmptyState
        heading="No portfolio yet"
        description="Your current template and latest deployment will show here once you've started one."
        action={
          <Button href="/dashboard/portfolio/editor" variant="primary">
            Start your portfolio
          </Button>
        }
      />
    );
  }

  return (
    <div className="rounded-[var(--radius)] border border-[var(--color-line)] bg-[var(--color-surface)] p-5">
      <h2 className="text-sm font-semibold text-[var(--color-ink-muted)]">Portfolio</h2>
      <p className="mt-2 font-medium">{state.resource.content.profile.displayName}</p>
      <p className="mt-1 text-sm text-[var(--color-ink-muted)]">
        {state.resource.content.projects.length} projects · {state.resource.content.skills.length} skills
      </p>
      {!state.resource.activeTemplateVersionId ? (
        <div className="mt-2">
          <Badge tone="warn">No template selected</Badge>
        </div>
      ) : null}
      <Button href="/dashboard/portfolio" variant="secondary" className="mt-4">
        View portfolio
      </Button>
    </div>
  );
}

export default function DashboardOverviewPage() {
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl">Overview</h1>
      <div className="grid gap-6 md:grid-cols-2">
        <BackendStatusCard />
        <PortfolioStatusCard />
      </div>
    </div>
  );
}
