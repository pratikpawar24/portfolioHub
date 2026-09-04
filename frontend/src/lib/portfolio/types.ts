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
