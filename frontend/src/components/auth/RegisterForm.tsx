"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { registerFormSchema, type RegisterFormValues } from "@/lib/validation/auth";
import { applyServerFieldErrors } from "@/lib/validation/applyServerFieldErrors";
import { useAuth } from "@/lib/auth/AuthContext";
import { ApiError } from "@/lib/api/client";
import { TextField } from "@/components/ui/TextField";
import { ErrorState } from "@/components/states/ErrorState";
import { Button } from "@/components/ui/Button";

const FIELDS = ["email", "username", "password", "confirmPassword"] as const;

export function RegisterForm() {
  const router = useRouter();
  const { register: createAccount } = useAuth();
  const [generalError, setGeneralError] = useState<ApiError | null>(null);
  const {
    register,
    handleSubmit,
    setError,
    formState: { errors, isSubmitting },
  } = useForm<RegisterFormValues>({ resolver: zodResolver(registerFormSchema) });

  async function onSubmit(values: RegisterFormValues) {
    setGeneralError(null);
    try {
      await createAccount({
        email: values.email,
        username: values.username,
        password: values.password,
      });
      router.push("/dashboard");
    } catch (err) {
      if (err instanceof ApiError) {
        const mapped = applyServerFieldErrors(err, setError, FIELDS);
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
        id="username"
        label="Username"
        hint="Your public URL will be portfoliohub.com/p/username"
        autoComplete="username"
        error={errors.username?.message}
        {...register("username")}
      />
      <TextField
        id="password"
        label="Password"
        type="password"
        autoComplete="new-password"
        error={errors.password?.message}
        {...register("password")}
      />
      <TextField
        id="confirmPassword"
        label="Confirm password"
        type="password"
        autoComplete="new-password"
        error={errors.confirmPassword?.message}
        {...register("confirmPassword")}
      />
      {generalError ? (
        <ErrorState
          kind={generalError.kind}
          message={generalError.body?.message}
          requestId={generalError.body?.requestId}
          onRetry={() => handleSubmit(onSubmit)()}
        />
      ) : null}
      <Button type="submit" disabled={isSubmitting}>
        {isSubmitting ? "Creating account…" : "Create account"}
      </Button>
    </form>
  );
}
