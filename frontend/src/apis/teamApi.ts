import axiosClient from './axiosClient'

// Owner: Oscar (Person 2) — UC-7,8,9,10,12,13,14
export const teamApi = {
  search(params?: { name?: string; sectionId?: number }) {
    return axiosClient.get('/api/teams', { params })
  },
  getById(id: number) {
    return axiosClient.get(`/api/teams/${id}`)
  },
  create(data: { name: string; description?: string; websiteUrl?: string; sectionId: number }) {
    return axiosClient.post('/api/teams', data)
  },
  update(id: number, data: { name: string; description?: string; websiteUrl?: string; sectionId?: number }) {
    return axiosClient.put(`/api/teams/${id}`, data)
  },
  remove(id: number) {
    return axiosClient.delete(`/api/teams/${id}`)
  },
  assignStudents(teamId: number, studentIds: number[]) {
    return axiosClient.post(`/api/teams/${teamId}/students`, studentIds)
  },
  removeStudent(teamId: number, studentId: number) {
    return axiosClient.delete(`/api/teams/${teamId}/students/${studentId}`)
  },
}
