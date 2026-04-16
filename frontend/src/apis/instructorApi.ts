import axiosClient from './axiosClient'

// Owner: Whitney (Person 3)
export const instructorApi = {
  invite(emails: string[]) {
    return axiosClient.post('/api/instructors/invite', emails)
  },
}
