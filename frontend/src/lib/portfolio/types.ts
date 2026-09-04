/**
 * Mirrors PortfolioSchema.md §2 (the canonical document example) field
 * for field. Where that doc shows an empty array with no example item
 * (experience, education, certifications, achievements, services,
 * testimonials, customSections), the item shape is genuinely undefined —
 * typed `unknown[]` here rather than guessed at, and the editor doesn't
 * build forms for them. See PHASE_2_NOTES.md.
 */
export interface AssetRef {
  assetId: string;
}

export interface ProfileSection {
  displayName: string;
  headline?: string;
  bio?: string;
  avatar?: AssetRef | null;
  location?: string;
  availability?: string;
}

export interface LinkItem {
  type: string;
  label: string;
  url: string;
}

export interface SkillItem {
  name: string;
  category?: string;
  level?: string;
}

export interface ProjectItem {
  id: string;
  title: string;
  summary?: string;
  description?: string;
  technologies: string[];
  githubUrl?: string;
  liveUrl?: string;
  image?: AssetRef | null;
  featured?: boolean;
}

export interface PortfolioDocument {
  schemaVersion: string;
  profile: ProfileSection;
  links: LinkItem[];
  skills: SkillItem[];
  projects: ProjectItem[];
  // Shape not documented anywhere in PortfolioSchema.md — preserved
  // as-is on save, never edited or re-shaped by this frontend.
  experience: unknown[];
  education: unknown[];
  certifications: unknown[];
  achievements: unknown[];
  services: unknown[];
  testimonials: unknown[];
  customSections: unknown[];
}

/**
 * Corrects a Phase 2 assumption. DatabaseDesign.md §2 shows `portfolios`
 * as its own row — id, slug, title, status, active_template_version_id,
 * current_draft_revision_id, published_revision_id — distinct from
 * `portfolio_revisions.content` (the JSONB matching PortfolioDocument
 * above). Phase 2 treated GET /portfolios/me as if it returned
 * PortfolioDocument directly; it almost certainly returns this entity
 * instead, since Phase 3 (template selection) needs
 * activeTemplateVersionId and that field isn't part of the content
 * schema. See PHASE_3_NOTES.md.
 */
export interface PortfolioMeta {
  id: string;
  slug: string;
  title: string;
  status: string; // real enum unconfirmed
  activeTemplateVersionId: string | null;
}

export interface PortfolioResource extends PortfolioMeta {
  content: PortfolioDocument;
}

export const EMPTY_PORTFOLIO_DOCUMENT: PortfolioDocument = {
  schemaVersion: "1.0",
  profile: { displayName: "" },
  links: [],
  skills: [],
  projects: [],
  experience: [],
  education: [],
  certifications: [],
  achievements: [],
  services: [],
  testimonials: [],
  customSections: [],
};

export const EMPTY_PORTFOLIO_RESOURCE: PortfolioResource = {
  id: "",
  slug: "",
  title: "",
  status: "draft",
  activeTemplateVersionId: null,
  content: EMPTY_PORTFOLIO_DOCUMENT,
};
