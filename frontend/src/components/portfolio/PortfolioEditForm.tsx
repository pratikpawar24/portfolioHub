"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { portfolioEditFormSchema, type PortfolioEditFormValues } from "@/lib/validation/portfolio";
import { applyServerFieldErrors } from "@/lib/validation/applyServerFieldErrors";
import { documentToFormValues, formValuesToDocument } from "@/lib/portfolio/formMapping";
import { saveMyPortfolioContent } from "@/lib/portfolio/api";
import type { PortfolioResource } from "@/lib/portfolio/types";
import { ApiError } from "@/lib/api/client";
import { ProfileFields } from "./ProfileFields";
import { LinksFields } from "./LinksFields";
import { SkillsFields } from "./SkillsFields";
import { ProjectsFields } from "./ProjectsFields";
import { UndocumentedSections } from "./UndocumentedSections";
import { ErrorState } from "@/components/states/ErrorState";
import { Button } from "@/components/ui/Button";

// Best-effort list for mapping server 422 details to fields — only the
// flat profile fields, since array-item field names (e.g. for a specific
// link or project) aren't predictable without a real contract. See
// PHASE_2_NOTES.md.
const MAPPABLE_FIELDS = [
  "profile.displayName",
  "profile.headline",
  "profile.bio",
  "profile.location",
  "profile.availability",
] as const;

export function PortfolioEditForm({ initialResource }: { initialResource: PortfolioResource }) {
  const [resource, setResource] = useState(initialResource);
  const [saveError, setSaveError] = useState<ApiError | null>(null);
  const [justSaved, setJustSaved] = useState(false);

  const {
    register,
    control,
    handleSubmit,
    setError,
    reset,
    formState: { errors, isSubmitting, isDirty },
  } = useForm<PortfolioEditFormValues>({
    resolver: zodResolver(portfolioEditFormSchema),
    defaultValues: documentToFormValues(initialResource.content),
  });

  async function onSubmit(values: PortfolioEditFormValues) {
    setSaveError(null);
    setJustSaved(false);
    try {
      const savedContent = await saveMyPortfolioContent(formValuesToDocument(values, resource.content));
      setResource((prev) => ({ ...prev, content: savedContent }));
      reset(documentToFormValues(savedContent));
      setJustSaved(true);
    } catch (err) {
      if (err instanceof ApiError) {
        const mapped = applyServerFieldErrors(err, setError, MAPPABLE_FIELDS);
        if (!mapped) setSaveError(err);
      } else {
        throw err;
      }
    }
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} noValidate className="flex flex-col gap-10 pb-24">
      <div className="flex items-center justify-between rounded-[var(--radius)] border border-[var(--color-line)] bg-[var(--color-surface)] px-4 py-3 text-sm">
        <span className="text-[var(--color-ink-muted)]">
          {resource.activeTemplateVersionId ? "Template applied" : "No template selected yet"}
        </span>
        <Button href="/dashboard/portfolio/templates" variant="secondary">
          {resource.activeTemplateVersionId ? "Change template" : "Choose a template"}
        </Button>
      </div>

      <ProfileFields register={register} errors={errors} />
      <LinksFields control={control} register={register} errors={errors} />
      <SkillsFields control={control} register={register} errors={errors} />
      <ProjectsFields control={control} register={register} errors={errors} />
      <UndocumentedSections />

      {saveError ? (
        <ErrorState
          kind={saveError.kind}
          message={saveError.body?.message}
          requestId={saveError.body?.requestId}
          onRetry={() => handleSubmit(onSubmit)()}
        />
      ) : null}

      <div className="sticky bottom-0 flex items-center gap-4 border-t border-[var(--color-line)] bg-[var(--color-canvas)] py-4">
        <Button type="submit" disabled={isSubmitting || !isDirty}>
          {isSubmitting ? "Saving…" : "Save"}
        </Button>
        {justSaved && !isDirty ? (
          <span className="text-sm text-[var(--color-signal-live)]">Saved</span>
        ) : null}
      </div>
    </form>
  );
}
