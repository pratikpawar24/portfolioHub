import { apiFetch } from "@/lib/api/client";
import { AUTH_PATHS } from "@/lib/api/config";
import type { AuthUser, LoginInput, RegisterInput } from "./types";

export function registerAccount(input: RegisterInput) {
  return apiFetch<AuthUser>({
    path: AUTH_PATHS.register,
    method: "POST",
    body: JSON.stringify(input),
  });
}

export function login(input: LoginInput) {
  return apiFetch<AuthUser>({
    path: AUTH_PATHS.login,
    method: "POST",
    body: JSON.stringify(input),
  });
}

export function logout() {
  return apiFetch<void>({ path: AUTH_PATHS.logout, method: "POST" });
}

export function getCurrentUser() {
  return apiFetch<AuthUser>({ path: AUTH_PATHS.me, method: "GET" });
}
