import type { FieldValues, Path, UseFormSetError } from "react-hook-form";
import type { ApiError } from "@/lib/api/client";

/**
 * BackendFrontendContract.md defines `details: unknown[]` on the error
 * envelope but not its shape. This assumes the common
 * `{ field, message }` convention and applies whichever entries match
 * it to form fields via react-hook-form's setError. Anything that
 * doesn't match is left for the caller to show as a general error —
 * this never throws or asserts a shape it can't confirm.
 */
export function applyServerFieldErrors<T extends FieldValues>(
  error: ApiError,
  setError: UseFormSetError<T>,
  validFields: readonly string[],
): boolean {
  if (error.kind !== "validation" || !Array.isArray(error.body?.details)) {
    return false;
  }

  let applied = false;
  for (const entry of error.body.details) {
    if (
      entry &&
      typeof entry === "object" &&
      "field" in entry &&
      "message" in entry &&
      typeof (entry as { field: unknown }).field === "string" &&
      typeof (entry as { message: unknown }).message === "string" &&
      validFields.includes((entry as { field: string }).field)
    ) {
      setError((entry as { field: string }).field as Path<T>, {
        type: "server",
        message: (entry as { message: string }).message,
      });
      applied = true;
    }
  }
  return applied;
}
