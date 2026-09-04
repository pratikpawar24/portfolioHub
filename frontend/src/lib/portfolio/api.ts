import { apiFetch } from "@/lib/api/client";
import { PORTFOLIO_PATHS } from "@/lib/api/config";
import type { PortfolioDocument } from "./types";

export function getMyPortfolio() {
  return apiFetch<PortfolioDocument>({ path: PORTFOLIO_PATHS.me, method: "GET" });
}

export function saveMyPortfolio(document: PortfolioDocument) {
  return apiFetch<PortfolioDocument>({
    path: PORTFOLIO_PATHS.me,
    method: "PUT",
    body: JSON.stringify(document),
  });
}
