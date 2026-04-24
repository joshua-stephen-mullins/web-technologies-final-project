import { defineStore } from 'pinia'
import { evaluationApi } from '@/apis/evaluationApi'

// Owner: Whitney (Person 3)
// UC-28,29,31,33,34
export const useEvaluationStore = defineStore('evaluation', {
  state: () => ({
    myReport: null as any,
    sectionReport: null as any,
    studentReport: null as any,
    loading: false,
  }),
  actions: {
    async fetchMyReport(weekId: number) {
      this.loading = true
      try {
        const res = await evaluationApi.getMyReport(weekId)
        this.myReport = res.data.data
      } finally {
        this.loading = false
      }
    },
    async fetchSectionReport(sectionId: number, weekId: number) {
      this.loading = true
      try {
        const res = await evaluationApi.getSectionReport(sectionId, weekId)
        this.sectionReport = res.data.data
      } finally {
        this.loading = false
      }
    },
    async fetchStudentReport(studentId: number, startWeekId: number, endWeekId: number) {
      this.loading = true
      try {
        const res = await evaluationApi.getStudentReport(studentId, startWeekId, endWeekId)
        this.studentReport = res.data.data
      } finally {
        this.loading = false
      }
    },
    // TODO: fetchStudentWARReport(studentId, start, end)
  },
})
