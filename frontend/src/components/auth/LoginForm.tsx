"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { loginFormSchema, type LoginFormValues } from "@/lib/validation/auth";
import { applyServerFieldErrors } from "@/lib/validation/applyServerFieldErrors";
import { useAuth } from "@/lib/auth/AuthContext";
import { ApiError } from "@/lib/api/client";
import { TextField } from "@/components/ui/TextField";
import { ErrorState } from "@/components/states/ErrorState";
import { Button } from "@/components/ui/Button";

const FIELDS = ["email", "password"] as const;

export function LoginForm() {
  const router = useRouter();
  const { login } = useAuth();
  const [generalError, setGeneralError] = useState<ApiError | null>(null);
  const {
    register,
    handleSubmit,
    setError,
    formState: { errors, isSubmitting },
  } = useForm<LoginFormValues>({ resolver: zodResolver(loginFormSchema) });

  async function onSubmit(values: LoginFormValues) {
    setGeneralError(null);
    try {
      await login(values);
      router.push("/dashboard");
    } catch (err) {
      if (err instanceof ApiError) {
        const mapped = applyServerFieldErrors(err, setError, FIELDS);
        // A bad email/password pair is normally 401, which isn't a form
        // validation error — surface it as one general message instead
        // of pretending we know which field was wrong.
        if (!mapped) setGeneralError(err);
      } else {
        throw err;
      }
    }
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} noValidate className="flex flex-col gap-4">
      <TextField
        id="email"
        label="Email"
        type="email"
        autoComplete="email"
        error={errors.email?.message}
        {...register("email")}
      />
      <TextField
        id="password"
        label="Password"
        type="password"
        autoComplete="current-password"
        error={errors.password?.message}
        {...register("password")}
      />
      {generalError ? (
        <ErrorState
          kind={generalError.kind === "unauthorized" ? "validation" : generalError.kind}
          message={
            generalError.kind === "unauthorized"
              ? "Incorrect email or password."
              : generalError.body?.message
          }
          requestId={generalError.body?.requestId}
          onRetry={() => handleSubmit(onSubmit)()}
        />
      ) : null}
      <Button type="submit" disabled={isSubmitting}>
        {isSubmitting ? "Signing in…" : "Sign in"}
      </Button>
    </form>
  );
}
