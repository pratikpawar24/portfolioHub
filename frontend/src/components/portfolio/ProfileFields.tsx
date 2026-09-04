import type { FieldErrors, UseFormRegister } from "react-hook-form";
import type { PortfolioEditFormValues } from "@/lib/validation/portfolio";
import { TextField } from "@/components/ui/TextField";
import { TextareaField } from "@/components/ui/Textarea";

interface ProfileFieldsProps {
  register: UseFormRegister<PortfolioEditFormValues>;
  errors: FieldErrors<PortfolioEditFormValues>;
}

export function ProfileFields({ register, errors }: ProfileFieldsProps) {
  return (
    <section aria-labelledby="profile-heading" className="flex flex-col gap-4">
      <h2 id="profile-heading" className="text-lg font-semibold">
        Profile
      </h2>
      <TextField
        id="profile.displayName"
        label="Name"
        error={errors.profile?.displayName?.message}
        {...register("profile.displayName")}
      />
      <TextField
        id="profile.headline"
        label="Headline"
        hint="A one-line summary, e.g. \u201cFull Stack Developer\u201d"
        error={errors.profile?.headline?.message}
        {...register("profile.headline")}
      />
      <TextareaField
        id="profile.bio"
        label="Bio"
        error={errors.profile?.bio?.message}
        {...register("profile.bio")}
      />
      <TextField
        id="profile.location"
        label="Location"
        error={errors.profile?.location?.message}
        {...register("profile.location")}
      />
      <TextField
        id="profile.availability"
        label="Availability"
        hint="e.g. \u201cOpen to opportunities\u201d"
        error={errors.profile?.availability?.message}
        {...register("profile.availability")}
      />
    </section>
  );
}
