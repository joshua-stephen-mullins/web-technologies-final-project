import axiosClient from './axiosClient'

export const rubricApi = {
  getAll() {
    return axiosClient.get('/api/rubrics')
  },
  getById(id: number) {
    return axiosClient.get(`/api/rubrics/${id}`)
  },
  create(data: { name: string; criteria: any[] }) {
    return axiosClient.post('/api/rubrics', data)
  },
  update(id: number, data: { name: string; criteria: any[] }) {
    return axiosClient.put(`/api/rubrics/${id}`, data)
  },
  remove(id: number) {
    return axiosClient.delete(`/api/rubrics/${id}`)
  },
}
