"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { DashboardNav } from "@/components/layout/DashboardNav";
import { SkipLink } from "@/components/layout/SkipLink";
import { RequireAuth } from "@/components/auth/RequireAuth";
import { useAuth } from "@/lib/auth/AuthContext";

function LogoutButton() {
  const { logout } = useAuth();
  const router = useRouter();

  async function handleLogout() {
    await logout();
    router.push("/");
  }

  return (
    <button
      type="button"
      onClick={handleLogout}
      className="text-sm text-[var(--color-ink-muted)] hover:text-[var(--color-ink)]"
    >
      Log out
    </button>
  );
}

export default function DashboardLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="min-h-screen">
      <SkipLink />
      <header className="border-b border-[var(--color-line)]">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-3">
          <Link href="/" className="font-[var(--font-display)] text-base font-semibold">
            PortfolioHub
          </Link>
          <div className="flex items-center gap-6">
            <Link href="/" className="text-sm text-[var(--color-ink-muted)] hover:text-[var(--color-ink)]">
              View public site
            </Link>
            <LogoutButton />
          </div>
        </div>
      </header>
      <main id="main" className="mx-auto flex max-w-6xl gap-8 px-6 py-8">
        <RequireAuth>
          <aside className="w-48 shrink-0">
            <DashboardNav />
          </aside>
          <div className="min-w-0 flex-1">{children}</div>
        </RequireAuth>
      </main>
    </div>
  );
}
