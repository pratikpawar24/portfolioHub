"use client";

import { useEffect, useState } from "react";
import { getHealth, type HealthStatus } from "@/lib/api/client";
import { LoadingState } from "@/components/states/LoadingState";
import { EmptyState } from "@/components/states/EmptyState";
import { Badge } from "@/components/ui/Badge";

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
            The API responded to a health check. Ready for Phase 1.
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

export default function DashboardOverviewPage() {
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl">Overview</h1>
      <div className="grid gap-6 md:grid-cols-2">
        <BackendStatusCard />
        <EmptyState
          heading="No portfolio yet"
          description="Your portfolio status, current template, and latest deployment will show here once the portfolio API is connected."
        />
      </div>
    </div>
  );
}
