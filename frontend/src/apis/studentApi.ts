// Owner: Oscar (Person 2) — UC-11,15,16,17
export const studentApi = {
  search(params?: { firstName?: string; lastName?: string; email?: string; teamId?: number }) {
    return axiosClient.get('/api/students', { params })
  },
  getById(id: number) {
    return axiosClient.get(`/api/students/${id}`)
  },
  remove(id: number) {
    return axiosClient.delete(`/api/students/${id}`)
  },
  invite(emails: string[], sectionId?: number) {
    return axiosClient.post('/api/students/invite', { emails, sectionId })
  },
}
