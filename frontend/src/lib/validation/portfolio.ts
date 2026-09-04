import { z } from "zod";

const optionalUrl = z
  .string()
  .trim()
  .refine((v) => v === "" || z.string().url().safeParse(v).success, "Enter a valid URL")
  .optional();

export const profileFormSchema = z.object({
  displayName: z.string().trim().min(1, "Required"),
  headline: z.string().trim().max(120, "Keep it under 120 characters").optional(),
  bio: z.string().trim().max(2000, "Keep it under 2000 characters").optional(),
  location: z.string().trim().optional(),
  availability: z.string().trim().optional(),
});

export const linkFormSchema = z.object({
  type: z.string().trim().min(1, "Choose a type"),
  label: z.string().trim().min(1, "Required"),
  url: z.string().trim().min(1, "Required").pipe(z.string().url("Enter a valid URL")),
});

export const skillFormSchema = z.object({
  name: z.string().trim().min(1, "Required"),
  category: z.string().trim().optional(),
  level: z.string().trim().optional(),
});

export const projectFormSchema = z.object({
  id: z.string(),
  title: z.string().trim().min(1, "Required"),
  summary: z.string().trim().max(200, "Keep it under 200 characters").optional(),
  description: z.string().trim().optional(),
  // Comma-separated in the form for a simple text input; split/joined
  // to/from PortfolioDocument.technologies (string[]) at the API boundary.
  technologies: z.string().trim().optional(),
  githubUrl: optionalUrl,
  liveUrl: optionalUrl,
  featured: z.boolean().optional(),
});

export const portfolioEditFormSchema = z.object({
  profile: profileFormSchema,
  links: z.array(linkFormSchema),
  skills: z.array(skillFormSchema),
  projects: z.array(projectFormSchema),
});

export type PortfolioEditFormValues = z.infer<typeof portfolioEditFormSchema>;
