import axiosClient from './axiosClient'

export const authApi = {
  login(username: string, password: string) {
    return axiosClient.post('/api/auth/login', { username, password })
  },
}
