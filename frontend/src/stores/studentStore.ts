import { defineStore } from 'pinia'
import { studentApi } from '@/apis/studentApi'

// Owner: Oscar (Person 2)
// UC-11,12,13,15,16,17
export const useStudentStore = defineStore('student', {
  state: () => ({
    students: [] as any[],
    currentStudent: null as any,
    loading: false,
  }),
  actions: {
    async fetchStudents(params?: { firstName?: string; lastName?: string; email?: string; teamId?: number }) {
      this.loading = true
      try {
        const res = await studentApi.search(params)
        this.students = res.data.data
      } finally {
        this.loading = false
      }
    },
    async fetchStudentById(id: number) {
      this.loading = true
      try {
        const res = await studentApi.getById(id)
        this.currentStudent = res.data.data
      } finally {
        this.loading = false
      }
    },
    async deleteStudent(id: number) {
      await studentApi.remove(id)
      this.students = this.students.filter((s) => s.id !== id)
      if (this.currentStudent?.id === id) this.currentStudent = null
    },
    async inviteStudents(emails: string[], sectionId?: number) {
      const res = await studentApi.invite(emails, sectionId)
      return res.data
    },
  },
})
