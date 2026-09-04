import { useFieldArray, type Control, type FieldErrors, type UseFormRegister } from "react-hook-form";
import type { PortfolioEditFormValues } from "@/lib/validation/portfolio";
import { TextField } from "@/components/ui/TextField";
import { Button } from "@/components/ui/Button";

interface SkillsFieldsProps {
  control: Control<PortfolioEditFormValues>;
  register: UseFormRegister<PortfolioEditFormValues>;
  errors: FieldErrors<PortfolioEditFormValues>;
}

export function SkillsFields({ control, register, errors }: SkillsFieldsProps) {
  const { fields, append, remove } = useFieldArray({ control, name: "skills" });

  return (
    <section aria-labelledby="skills-heading" className="flex flex-col gap-4">
      <h2 id="skills-heading" className="text-lg font-semibold">
        Skills
      </h2>
      {fields.length === 0 ? (
        <p className="text-sm text-[var(--color-ink-muted)]">No skills added yet.</p>
      ) : null}
      {fields.map((field, index) => (
        <div
          key={field.id}
          className="grid gap-3 rounded-[var(--radius)] border border-[var(--color-line)] p-4 sm:grid-cols-[2fr_1fr_1fr_auto] sm:items-end"
        >
          <TextField
            id={`skills.${index}.name`}
            label="Skill"
            error={errors.skills?.[index]?.name?.message}
            {...register(`skills.${index}.name`)}
          />
          <TextField
            id={`skills.${index}.category`}
            label="Category"
            hint="Optional"
            error={errors.skills?.[index]?.category?.message}
            {...register(`skills.${index}.category`)}
          />
          <TextField
            id={`skills.${index}.level`}
            label="Level"
            hint="Optional"
            error={errors.skills?.[index]?.level?.message}
            {...register(`skills.${index}.level`)}
          />
          <Button type="button" variant="secondary" onClick={() => remove(index)}>
            Remove
          </Button>
        </div>
      ))}
      <Button
        type="button"
        variant="secondary"
        onClick={() => append({ name: "", category: "", level: "" })}
      >
        Add skill
      </Button>
    </section>
  );
}
