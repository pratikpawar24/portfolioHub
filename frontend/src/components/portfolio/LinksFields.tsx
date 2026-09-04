import { useFieldArray, type Control, type FieldErrors, type UseFormRegister } from "react-hook-form";
import type { PortfolioEditFormValues } from "@/lib/validation/portfolio";
import { TextField } from "@/components/ui/TextField";
import { Button } from "@/components/ui/Button";

interface LinksFieldsProps {
  control: Control<PortfolioEditFormValues>;
  register: UseFormRegister<PortfolioEditFormValues>;
  errors: FieldErrors<PortfolioEditFormValues>;
}

export function LinksFields({ control, register, errors }: LinksFieldsProps) {
  const { fields, append, remove } = useFieldArray({ control, name: "links" });

  return (
    <section aria-labelledby="links-heading" className="flex flex-col gap-4">
      <h2 id="links-heading" className="text-lg font-semibold">
        Links
      </h2>
      {fields.length === 0 ? (
        <p className="text-sm text-[var(--color-ink-muted)]">No links added yet.</p>
      ) : null}
      {fields.map((field, index) => (
        <div
          key={field.id}
          className="grid gap-3 rounded-[var(--radius)] border border-[var(--color-line)] p-4 sm:grid-cols-[1fr_1fr_2fr_auto] sm:items-end"
        >
          <TextField
            id={`links.${index}.type`}
            label="Type"
            hint="e.g. github, linkedin, website"
            error={errors.links?.[index]?.type?.message}
            {...register(`links.${index}.type`)}
          />
          <TextField
            id={`links.${index}.label`}
            label="Label"
            error={errors.links?.[index]?.label?.message}
            {...register(`links.${index}.label`)}
          />
          <TextField
            id={`links.${index}.url`}
            label="URL"
            type="url"
            error={errors.links?.[index]?.url?.message}
            {...register(`links.${index}.url`)}
          />
          <Button type="button" variant="secondary" onClick={() => remove(index)}>
            Remove
          </Button>
        </div>
      ))}
      <Button
        type="button"
        variant="secondary"
        onClick={() => append({ type: "", label: "", url: "" })}
      >
        Add link
      </Button>
    </section>
  );
}
