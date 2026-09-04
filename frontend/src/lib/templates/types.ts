/**
 * NOT CONFIRMED as an API shape. Derived from two sources that don't
 * quite line up on their own:
 *  - DatabaseDesign.md §2: `templates` (id, creator_user_id nullable,
 *    slug, name, description, category, license, repository_url,
 *    visibility, status) and `template_versions` (id, template_id,
 *    version, manifest JSONB, schema_min, schema_max, preview_reference)
 *    as two separate rows.
 *  - TemplateContract.md §2: the manifest itself (framework, runtime,
 *    build, capabilities).
 * Modeled here as `Template` (stable identity) + `currentVersion`
 * (whichever template_versions row is active/approved) rather than one
 * flat object, since framework/capabilities/schema-compatibility belong
 * to a specific version, not the template as a whole. See
 * PHASE_3_NOTES.md.
 */
export interface TemplateCreator {
  userId: string;
  displayName: string;
  profileUrl?: string;
}

export interface TemplateSchemaCompatibility {
  min: string;
  max: string;
}

export interface TemplateCapabilities {
  staticHosting: boolean;
  customFonts: boolean;
  darkMode: boolean;
}

export interface TemplateVersionSummary {
  id: string;
  version: string;
  framework: string;
  portfolioSchema: TemplateSchemaCompatibility;
  capabilities: TemplateCapabilities;
  previewImageUrl?: string;
}

export interface TemplateSummary {
  id: string;
  slug: string;
  name: string;
  category?: string;
  license: string;
  // creator_user_id is nullable in the DB for platform/first-party
  // templates — matches FrontendDevelopmentPrompt.md's "creator/author
  // attribution where available" (i.e. not always available).
  creator: TemplateCreator | null;
  currentVersion: TemplateVersionSummary;
}

export interface TemplateDetail extends TemplateSummary {
  description?: string;
  repositoryUrl?: string;
}
