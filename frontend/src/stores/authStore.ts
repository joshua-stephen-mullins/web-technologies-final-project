import { defineStore } from 'pinia'
import { authApi } from '@/apis/authApi'
import { userApi } from '@/apis/userApi'
import router from '@/router'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') ?? null as string | null,
    username: localStorage.getItem('username') ?? null as string | null,
    roles: localStorage.getItem('roles') ?? null as string | null,
  }),
  getters: {
    isAuthenticated: (state) => !!state.token,
    isAdmin: (state) => state.roles?.includes('ROLE_ADMIN') ?? false,
    isInstructor: (state) => state.roles?.includes('ROLE_INSTRUCTOR') ?? false,
    isStudent: (state) => state.roles?.includes('ROLE_STUDENT') ?? false,
  },
  actions: {
    async login(username: string, password: string) {
      const response = await authApi.login(username, password)
      const { token, roles } = response.data.data
      this.token = token
      this.username = username
      this.roles = roles
      localStorage.setItem('token', token)
      localStorage.setItem('username', username)
      localStorage.setItem('roles', roles)
      router.push('/')
    },
    logout() {
      this.token = null
      this.username = null
      this.roles = null
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('roles')
      router.push('/login')
    },
    async loadMe() {
      const response = await userApi.getMe()
      const user = response.data.data
      this.username = user.username
      this.roles = user.roles
      localStorage.setItem('username', user.username)
      localStorage.setItem('roles', user.roles)
    },
  },
})
