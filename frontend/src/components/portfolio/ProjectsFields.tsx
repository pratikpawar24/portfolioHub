import { useFieldArray, type Control, type FieldErrors, type UseFormRegister } from "react-hook-form";
import type { PortfolioEditFormValues } from "@/lib/validation/portfolio";
import { TextField } from "@/components/ui/TextField";
import { TextareaField } from "@/components/ui/Textarea";
import { Button } from "@/components/ui/Button";
import { newProjectId } from "@/lib/portfolio/formMapping";

interface ProjectsFieldsProps {
  control: Control<PortfolioEditFormValues>;
  register: UseFormRegister<PortfolioEditFormValues>;
  errors: FieldErrors<PortfolioEditFormValues>;
}

export function ProjectsFields({ control, register, errors }: ProjectsFieldsProps) {
  const { fields, append, remove } = useFieldArray({ control, name: "projects" });

  return (
    <section aria-labelledby="projects-heading" className="flex flex-col gap-4">
      <h2 id="projects-heading" className="text-lg font-semibold">
        Projects
      </h2>
      {fields.length === 0 ? (
        <p className="text-sm text-[var(--color-ink-muted)]">No projects added yet.</p>
      ) : null}
      {fields.map((field, index) => (
        <div
          key={field.id}
          className="flex flex-col gap-3 rounded-[var(--radius)] border border-[var(--color-line)] p-4"
        >
          <TextField
            id={`projects.${index}.title`}
            label="Title"
            error={errors.projects?.[index]?.title?.message}
            {...register(`projects.${index}.title`)}
          />
          <TextField
            id={`projects.${index}.summary`}
            label="Short summary"
            hint="One line — shown in compact template layouts"
            error={errors.projects?.[index]?.summary?.message}
            {...register(`projects.${index}.summary`)}
          />
          <TextareaField
            id={`projects.${index}.description`}
            label="Full description"
            error={errors.projects?.[index]?.description?.message}
            {...register(`projects.${index}.description`)}
          />
          <TextField
            id={`projects.${index}.technologies`}
            label="Technologies"
            hint="Comma-separated, e.g. Java, Spring Boot, PostgreSQL"
            error={errors.projects?.[index]?.technologies?.message}
            {...register(`projects.${index}.technologies`)}
          />
          <div className="grid gap-3 sm:grid-cols-2">
            <TextField
              id={`projects.${index}.githubUrl`}
              label="GitHub URL"
              type="url"
              hint="Optional"
              error={errors.projects?.[index]?.githubUrl?.message}
              {...register(`projects.${index}.githubUrl`)}
            />
            <TextField
              id={`projects.${index}.liveUrl`}
              label="Live URL"
              type="url"
              hint="Optional"
              error={errors.projects?.[index]?.liveUrl?.message}
              {...register(`projects.${index}.liveUrl`)}
            />
          </div>
          <label className="flex items-center gap-2 text-sm">
            <input type="checkbox" {...register(`projects.${index}.featured`)} />
            Featured
          </label>
          <Button
            type="button"
            variant="secondary"
            onClick={() => remove(index)}
            className="self-start"
          >
            Remove project
          </Button>
        </div>
      ))}
      <Button
        type="button"
        variant="secondary"
        onClick={() =>
          append({
            id: newProjectId(),
            title: "",
            summary: "",
            description: "",
            technologies: "",
            githubUrl: "",
            liveUrl: "",
            featured: false,
          })
        }
      >
        Add project
      </Button>
    </section>
  );
}
