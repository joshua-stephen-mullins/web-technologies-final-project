import axiosClient from './axiosClient'

export const sectionApi = {
  search(name?: string) {
    return axiosClient.get('/api/sections', { params: name ? { name } : {} })
  },
  getById(id: number) {
    return axiosClient.get(`/api/sections/${id}`)
  },
  create(data: { name: string; startDate: string; endDate: string; rubricId?: number }) {
    return axiosClient.post('/api/sections', data)
  },
  update(id: number, data: { name: string; startDate: string; endDate: string; rubricId?: number }) {
    return axiosClient.put(`/api/sections/${id}`, data)
  },
  getWeeks(sectionId: number) {
    return axiosClient.get(`/api/sections/${sectionId}/weeks`)
  },
  updateActiveWeeks(sectionId: number, inactiveWeekNumbers: number[]) {
    return axiosClient.put(`/api/sections/${sectionId}/weeks`, { inactiveWeekNumbers })
  },
}
