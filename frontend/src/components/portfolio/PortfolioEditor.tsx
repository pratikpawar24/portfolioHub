"use client";

import { useEffect, useState } from "react";
import { getMyPortfolio } from "@/lib/portfolio/api";
import { EMPTY_PORTFOLIO_RESOURCE, type PortfolioResource } from "@/lib/portfolio/types";
import { ApiError } from "@/lib/api/client";
import { LoadingState } from "@/components/states/LoadingState";
import { ErrorState } from "@/components/states/ErrorState";
import { PortfolioEditForm } from "./PortfolioEditForm";

export type LoadResult =
  | { status: "loaded"; resource: PortfolioResource }
  | { status: "error"; error: ApiError };

export async function resolvePortfolio(): Promise<LoadResult> {
  try {
    const resource = await getMyPortfolio();
    return { status: "loaded", resource };
  } catch (err) {
    if (err instanceof ApiError && err.kind === "not_found") {
      // No portfolio created yet — that's not an error, it's day one.
      return { status: "loaded", resource: EMPTY_PORTFOLIO_RESOURCE };
    }
    if (err instanceof ApiError) {
      return { status: "error", error: err };
    }
    throw err;
  }
}

export function PortfolioEditor() {
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

  async function retry() {
    setState({ status: "loading" }); // fine here — event handler, not an effect
    setState(await resolvePortfolio());
  }

  if (state.status === "loading") {
    return <LoadingState label="Loading your portfolio…" />;
  }

  if (state.status === "error") {
    return (
      <ErrorState
        kind={state.error.kind}
        message={state.error.body?.message}
        requestId={state.error.body?.requestId}
        onRetry={retry}
      />
    );
  }

  return <PortfolioEditForm initialResource={state.resource} />;
}
