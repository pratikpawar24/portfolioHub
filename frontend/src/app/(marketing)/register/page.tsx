import type { Metadata } from "next";
import Link from "next/link";
import { RegisterForm } from "@/components/auth/RegisterForm";

export const metadata: Metadata = { title: "Create your portfolio" };

export default function RegisterPage() {
  return (
    <div className="mx-auto flex max-w-sm flex-col gap-6">
      <h1 className="text-2xl">Create your account</h1>
      <RegisterForm />
      <p className="text-sm text-[var(--color-ink-muted)]">
        Already have an account?{" "}
        <Link href="/login" className="font-medium text-[var(--color-accent)] hover:text-[var(--color-accent-strong)]">
          Log in
        </Link>
      </p>
    </div>
  );
}
