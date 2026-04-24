import axiosClient from './axiosClient'

// Owner: Oscar (Person 2) — UC-27,32
export const activityApi = {
  getMyWeeks() {
    return axiosClient.get('/api/activities/my-weeks')
  },
  getByWeek(weekId: number) {
    return axiosClient.get('/api/activities', { params: { weekId } })
  },
  create(data: { weekId: number; category: string; description: string; plannedHours: number; actualHours?: number; status: string }) {
    return axiosClient.post('/api/activities', data)
  },
  update(id: number, data: { weekId?: number; category: string; description: string; plannedHours: number; actualHours?: number; status: string }) {
    return axiosClient.put(`/api/activities/${id}`, data)
  },
  remove(id: number) {
    return axiosClient.delete(`/api/activities/${id}`)
  },
  getTeamWARReport(teamId: number, weekId: number) {
    return axiosClient.get('/api/activities/report/team', { params: { teamId, weekId } })
  },
  getStudentWARReport(studentId: number, weekIds: number[]) {
    return axiosClient.get(`/api/activities/report/student/${studentId}`, { params: { weekIds } })
  },
}
