"use client";

import Link from "next/link";
import { useAuth } from "@/lib/auth/AuthContext";
import { Badge } from "@/components/ui/Badge";

export default function SettingsPage() {
  const { user } = useAuth();

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl">Account</h1>

      <div className="rounded-[var(--radius)] border border-[var(--color-line)] bg-[var(--color-surface)] p-5">
        <dl className="flex flex-col gap-3 text-sm">
          <div className="flex items-center justify-between">
            <dt className="text-[var(--color-ink-muted)]">Email</dt>
            <dd>{user?.email}</dd>
          </div>
          <div className="flex items-center justify-between">
            <dt className="text-[var(--color-ink-muted)]">Username</dt>
            <dd className="flex items-center gap-2">
              {user?.username}
              <Badge>/p/{user?.username}</Badge>
            </dd>
          </div>
        </dl>
      </div>

      <p className="text-sm text-[var(--color-ink-muted)]">
        Changing your email or password isn&apos;t available yet.{" "}
        <Link
          href="/dashboard/settings/connections"
          className="font-medium text-[var(--color-accent)] hover:text-[var(--color-accent-strong)]"
        >
          View hosting connections
        </Link>
        .
      </p>
    </div>
  );
}
