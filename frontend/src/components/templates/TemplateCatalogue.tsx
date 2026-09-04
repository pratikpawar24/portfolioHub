"use client";

import { useEffect, useState } from "react";
import { listTemplates } from "@/lib/templates/api";
import type { TemplateSummary } from "@/lib/templates/types";
import { ApiError } from "@/lib/api/client";
import { LoadingState } from "@/components/states/LoadingState";
import { ErrorState } from "@/components/states/ErrorState";
import { EmptyState } from "@/components/states/EmptyState";
import { TemplateCard } from "./TemplateCard";

type LoadResult =
  | { status: "loaded"; templates: TemplateSummary[] }
  | { status: "error"; error: ApiError };

async function resolveTemplates(): Promise<LoadResult> {
  try {
    const templates = await listTemplates();
    return { status: "loaded", templates };
  } catch (err) {
    if (err instanceof ApiError) return { status: "error", error: err };
    throw err;
  }
}

export function TemplateCatalogue() {
  const [state, setState] = useState<LoadResult | { status: "loading" }>({ status: "loading" });

  useEffect(() => {
    let cancelled = false;
    resolveTemplates().then((result) => {
      if (!cancelled) setState(result);
    });
    return () => {
      cancelled = true;
    };
  }, []);

  async function retry() {
    setState({ status: "loading" });
    setState(await resolveTemplates());
  }

  if (state.status === "loading") {
    return <LoadingState label="Loading templates…" />;
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

  if (state.templates.length === 0) {
    return (
      <EmptyState
        heading="No templates available yet"
        description="Approved first-party templates will appear here."
      />
    );
  }

  return (
    <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
      {state.templates.map((template) => (
        <TemplateCard key={template.id} template={template} />
      ))}
    </div>
  );
}
