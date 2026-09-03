"use client";

import { useEffect, type ReactNode } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/lib/auth/AuthContext";
import { LoadingState } from "@/components/states/LoadingState";
import { ErrorState } from "@/components/states/ErrorState";

export function RequireAuth({ children }: { children: ReactNode }) {
  const { status, sessionCheckError, refresh } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (status === "anonymous" && !sessionCheckError) {
      router.replace("/login");
    }
  }, [status, sessionCheckError, router]);

  if (status === "checking") {
    return <LoadingState label="Checking your session…" />;
  }

  // Couldn't reach the API at all — distinct from "not signed in", and
  // worth a retry rather than a silent bounce to /login.
  if (sessionCheckError) {
    return (
      <ErrorState
        kind={sessionCheckError.kind}
        message={sessionCheckError.body?.message}
        requestId={sessionCheckError.body?.requestId}
        onRetry={refresh}
      />
    );
  }

  if (status === "anonymous") {
    // Redirect effect above handles navigation; render nothing meanwhile.
    return null;
  }

  return <>{children}</>;
}
