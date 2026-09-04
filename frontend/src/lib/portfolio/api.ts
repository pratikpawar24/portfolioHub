import { apiFetch } from "@/lib/api/client";
import { PORTFOLIO_PATHS } from "@/lib/api/config";
import type { PortfolioDocument, PortfolioResource } from "./types";

export function getMyPortfolio() {
  return apiFetch<PortfolioResource>({ path: PORTFOLIO_PATHS.me, method: "GET" });
}

export function saveMyPortfolioContent(document: PortfolioDocument) {
  return apiFetch<PortfolioDocument>({
    path: PORTFOLIO_PATHS.content,
    method: "PUT",
    body: JSON.stringify(document),
  });
}

export function setActiveTemplate(templateVersionId: string) {
  return apiFetch<PortfolioResource>({
    path: PORTFOLIO_PATHS.activeTemplate,
    method: "PUT",
    body: JSON.stringify({ templateVersionId }),
  });
}
