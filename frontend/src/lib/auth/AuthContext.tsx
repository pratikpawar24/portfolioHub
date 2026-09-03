"use client";

import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from "react";
import * as authApi from "./api";
import type { AuthUser, LoginInput, RegisterInput } from "./types";
import { ApiError } from "@/lib/api/client";

type AuthStatus = "checking" | "authenticated" | "anonymous";

interface AuthContextValue {
  user: AuthUser | null;
  status: AuthStatus;
  /** Set only when the session check itself failed to reach the API — lets
   *  RequireAuth tell "not signed in" apart from "can't reach the backend". */
  sessionCheckError: ApiError | null;
  login: (input: LoginInput) => Promise<void>;
  register: (input: RegisterInput) => Promise<void>;
  logout: () => Promise<void>;
  refresh: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [status, setStatus] = useState<AuthStatus>("checking");
  const [sessionCheckError, setSessionCheckError] = useState<ApiError | null>(null);

  const refresh = useCallback(async () => {
    try {
      const current = await authApi.getCurrentUser();
      setUser(current);
      setStatus("authenticated");
      setSessionCheckError(null);
    } catch (err) {
      setUser(null);
      setStatus("anonymous");
      // "unauthorized" just means no session — expected for a logged-out
      // visitor, not an error worth surfacing. Anything else (network,
      // server, ...) is a real connectivity problem RequireAuth should show.
      setSessionCheckError(err instanceof ApiError && err.kind !== "unauthorized" ? err : null);
    }
  }, []);

  useEffect(() => {
    let cancelled = false;
    authApi.getCurrentUser().then(
      (current) => {
        if (cancelled) return;
        setUser(current);
        setStatus("authenticated");
        setSessionCheckError(null);
      },
      (err) => {
        if (cancelled) return;
        setUser(null);
        setStatus("anonymous");
        setSessionCheckError(err instanceof ApiError && err.kind !== "unauthorized" ? err : null);
      },
    );
    return () => {
      cancelled = true;
    };
  }, []);

  const login = useCallback(async (input: LoginInput) => {
    const current = await authApi.login(input);
    setUser(current);
    setStatus("authenticated");
  }, []);

  const register = useCallback(async (input: RegisterInput) => {
    const current = await authApi.registerAccount(input);
    setUser(current);
    setStatus("authenticated");
  }, []);

  const logout = useCallback(async () => {
    try {
      await authApi.logout();
    } finally {
      // Clear local state even if the request failed — see PHASE_1_NOTES.md
      // for the tradeoff (the server-side session may outlive this).
      setUser(null);
      setStatus("anonymous");
    }
  }, []);

  return (
    <AuthContext.Provider value={{ user, status, sessionCheckError, login, register, logout, refresh }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within an AuthProvider");
  return ctx;
}
