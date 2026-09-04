import type { PortfolioDocument, ProjectItem } from "./types";
import type { PortfolioEditFormValues } from "@/lib/validation/portfolio";

export function documentToFormValues(doc: PortfolioDocument): PortfolioEditFormValues {
  return {
    profile: {
      displayName: doc.profile.displayName,
      headline: doc.profile.headline ?? "",
      bio: doc.profile.bio ?? "",
      location: doc.profile.location ?? "",
      availability: doc.profile.availability ?? "",
    },
    links: doc.links.map((l) => ({ type: l.type, label: l.label, url: l.url })),
    skills: doc.skills.map((s) => ({
      name: s.name,
      category: s.category ?? "",
      level: s.level ?? "",
    })),
    projects: doc.projects.map((p) => ({
      id: p.id,
      title: p.title,
      summary: p.summary ?? "",
      description: p.description ?? "",
      technologies: p.technologies.join(", "),
      githubUrl: p.githubUrl ?? "",
      liveUrl: p.liveUrl ?? "",
      featured: p.featured ?? false,
    })),
  };
}

/**
 * `base` supplies everything this editor doesn't touch: schemaVersion,
 * avatar/project images (no upload UI exists yet — see PHASE_2_NOTES.md),
 * and the sections with no documented item shape.
 */
export function formValuesToDocument(
  values: PortfolioEditFormValues,
  base: PortfolioDocument,
): PortfolioDocument {
  const originalProjectsById = new Map(base.projects.map((p) => [p.id, p]));

  return {
    ...base,
    profile: {
      displayName: values.profile.displayName.trim(),
      headline: values.profile.headline?.trim() || undefined,
      bio: values.profile.bio?.trim() || undefined,
      location: values.profile.location?.trim() || undefined,
      availability: values.profile.availability?.trim() || undefined,
      avatar: base.profile.avatar ?? null,
    },
    links: values.links.map((l) => ({
      type: l.type.trim(),
      label: l.label.trim(),
      url: l.url.trim(),
    })),
    skills: values.skills.map((s) => ({
      name: s.name.trim(),
      category: s.category?.trim() || undefined,
      level: s.level?.trim() || undefined,
    })),
    projects: values.projects.map((p): ProjectItem => {
      const original = originalProjectsById.get(p.id);
      return {
        id: p.id,
        title: p.title.trim(),
        summary: p.summary?.trim() || undefined,
        description: p.description?.trim() || undefined,
        technologies: p.technologies
          ? p.technologies
              .split(",")
              .map((t) => t.trim())
              .filter(Boolean)
          : [],
        githubUrl: p.githubUrl?.trim() || undefined,
        liveUrl: p.liveUrl?.trim() || undefined,
        featured: p.featured ?? false,
        image: original?.image ?? null,
      };
    }),
  };
}

/**
 * NOT CONFIRMED: whether the backend accepts/expects client-generated
 * ids for new project items, or assigns its own on save. UUID is a
 * placeholder assumption — see PHASE_2_NOTES.md.
 */
export function newProjectId(): string {
  return `project_${crypto.randomUUID()}`;
}
