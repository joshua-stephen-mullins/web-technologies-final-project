import axiosClient from './axiosClient'

export const userApi = {
  validateToken(token: string) {
    return axiosClient.get(`/api/users/register/validate?token=${token}`)
  },
  register(token: string, data: { firstName: string; lastName: string; password: string }) {
    return axiosClient.post(`/api/users/register?token=${token}`, data)
  },
  getMe() {
    return axiosClient.get('/api/users/me')
  },
  updateMe(data: { firstName?: string; lastName?: string; email?: string }) {
    return axiosClient.put('/api/users/me', data)
  },
  changePassword(data: { oldPassword: string; newPassword: string }) {
    return axiosClient.put('/api/users/me/password', data)
  },
  forgotPassword(email: string) {
    return axiosClient.post('/api/users/forgot-password', { email })
  },
  resetPassword(token: string, newPassword: string) {
    return axiosClient.post('/api/users/reset-password', { token, newPassword })
  },
}
