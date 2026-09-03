import type { Metadata } from "next";
import Link from "next/link";
import { LoginForm } from "@/components/auth/LoginForm";

export const metadata: Metadata = { title: "Log in" };

export default function LoginPage() {
  return (
    <div className="mx-auto flex max-w-sm flex-col gap-6">
      <h1 className="text-2xl">Log in</h1>
      <LoginForm />
      <p className="text-sm text-[var(--color-ink-muted)]">
        Don&apos;t have an account?{" "}
        <Link href="/register" className="font-medium text-[var(--color-accent)] hover:text-[var(--color-accent-strong)]">
          Create one
        </Link>
      </p>
    </div>
  );
}
