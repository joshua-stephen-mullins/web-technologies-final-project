import axiosClient from './axiosClient'

// Owner: Oscar (Person 2) — UC-7,8,9,10,12,13,14
export const teamApi = {
  listAll() {
    return axiosClient.get('/api/teams')
  },
  getById(id: number) {
    return axiosClient.get(`/api/teams/${id}`)
  },
  // TODO: search(params), create(data), update(id, data), remove(id),
  //       assignStudents(teamId, studentIds), removeStudent(teamId, studentId)
}
