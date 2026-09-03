import { z } from "zod";

/**
 * These are UX conveniences only, not the real policy — Requirements.md
 * §5 specifies password hashing but no complexity rule, and §3 says
 * slugs are "lowercase, URL-safe, length constrained, reserved words
 * blocked" without giving the exact length or the reserved-word list.
 * Reserved-word blocking deliberately isn't implemented here — that's a
 * server-owned business rule (FrontendDevelopmentPrompt.md §15: "do not
 * put business rules only in the frontend"), so it's left to the 409/422
 * response instead of being guessed here.
 */
export const usernameSchema = z
  .string()
  .min(3, "At least 3 characters")
  .max(30, "At most 30 characters")
  .regex(/^[a-z0-9-]+$/, "Lowercase letters, numbers and hyphens only");

export const emailSchema = z.string().email("Enter a valid email address");

export const passwordSchema = z.string().min(8, "At least 8 characters");

export const registerFormSchema = z
  .object({
    email: emailSchema,
    username: usernameSchema,
    password: passwordSchema,
    confirmPassword: z.string(),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "Passwords don't match",
    path: ["confirmPassword"],
  });

export type RegisterFormValues = z.infer<typeof registerFormSchema>;

export const loginFormSchema = z.object({
  email: emailSchema,
  password: z.string().min(1, "Enter your password"),
});

export type LoginFormValues = z.infer<typeof loginFormSchema>;
