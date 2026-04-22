import axiosClient from './axiosClient'

// Owner: Whitney (Person 3)
export const instructorApi = {
  invite(emails: string[]) {
    return axiosClient.post('/api/instructors/invite', emails)
  },
  listAll(params?: { firstName?: string; lastName?: string; teamName?: string; status?: string }) {
    return axiosClient.get('/api/instructors', { params })
  },
  getById(id: number) {
    return axiosClient.get(`/api/instructors/${id}`)
  },
  deactivate(id: number) {
    return axiosClient.patch(`/api/instructors/${id}/deactivate`)
  },
  getTeamInstructors(teamId: number) {
    return axiosClient.get(`/api/teams/${teamId}/instructors`)
  },
  assignToTeam(teamId: number, instructorIds: number[]) {
    return axiosClient.post(`/api/teams/${teamId}/instructors`, instructorIds)
  },
  removeFromTeam(teamId: number, instructorId: number) {
    return axiosClient.delete(`/api/teams/${teamId}/instructors/${instructorId}`)
  },
}
