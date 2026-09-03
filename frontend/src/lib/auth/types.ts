/**
 * Shape is a best guess, not a confirmed contract — PRD.md §7.1 only
 * says "User profile and public username/slug", nothing about exact
 * field names. Narrow/rename these the moment OpenAPI exists rather
 * than treating this file as settled.
 */
export interface AuthUser {
  id: string;
  email: string;
  username: string;
}

export interface RegisterInput {
  email: string;
  password: string;
  username: string;
}

export interface LoginInput {
  email: string;
  password: string;
}
