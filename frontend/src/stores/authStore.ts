import { defineStore } from 'pinia'

// Owner: Josh (Person 1)
export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') ?? null as string | null,
    username: null as string | null,
    roles: null as string | null,
  }),
  getters: {
    isAuthenticated: (state) => !!state.token,
    isAdmin: (state) => state.roles?.includes('ROLE_ADMIN') ?? false,
    isInstructor: (state) => state.roles?.includes('ROLE_INSTRUCTOR') ?? false,
    isStudent: (state) => state.roles?.includes('ROLE_STUDENT') ?? false,
  },
  actions: {
    // TODO: login(username, password): call POST /api/auth/login, store token
    // TODO: logout(): clear token and redirect
  },
})
