// Shared role helpers used in components to conditionally show UI elements

export function isAdmin(roles: string): boolean {
  return roles?.includes('ROLE_ADMIN') ?? false
}

export function isInstructor(roles: string): boolean {
  return roles?.includes('ROLE_INSTRUCTOR') ?? false
}

export function isStudent(roles: string): boolean {
  return roles?.includes('ROLE_STUDENT') ?? false
}
