import { apiFetch } from "@/lib/api/client";
import { TEMPLATE_PATHS } from "@/lib/api/config";
import type { TemplateDetail, TemplateSummary } from "./types";

export function listTemplates() {
  return apiFetch<TemplateSummary[]>({ path: TEMPLATE_PATHS.list, method: "GET" });
}

export function getTemplateDetail(slug: string) {
  return apiFetch<TemplateDetail>({ path: TEMPLATE_PATHS.detail(slug), method: "GET" });
}
