"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/lib/auth/AuthContext";
import { setActiveTemplate } from "@/lib/portfolio/api";
import { ApiError } from "@/lib/api/client";

export function useApplyTemplate() {
  const { status } = useAuth();
  const router = useRouter();
  const [applying, setApplying] = useState(false);
  const [error, setError] = useState<ApiError | null>(null);

  async function apply(templateVersionId: string) {
    if (status !== "authenticated") {
      router.push("/register");
      return;
    }
    setApplying(true);
    setError(null);
    try {
      await setActiveTemplate(templateVersionId);
      router.push("/dashboard/portfolio");
    } catch (err) {
      if (err instanceof ApiError) {
        setError(err);
      } else {
        throw err;
      }
    } finally {
      setApplying(false);
    }
  }

  return { apply, applying, error };
}
