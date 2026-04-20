import axiosClient from './axiosClient'

// Owner: Whitney (Person 3)
export const instructorApi = {
  invite(emails: string[]) {
    return axiosClient.post('/api/instructors/invite', emails)
  },
  listAll() {
    return axiosClient.get('/api/instructors')
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
