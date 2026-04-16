import { defineStore } from 'pinia'
import { instructorApi } from '@/apis/instructorApi'

// Owner: Whitney (Person 3)
export const useInstructorStore = defineStore('instructor', {
  state: () => ({
    instructors: [] as any[],
    currentInstructor: null as any,
    loading: false,
  }),
  actions: {
    async inviteInstructors(emails: string[]) {
      await instructorApi.invite(emails)
    },
  },
})