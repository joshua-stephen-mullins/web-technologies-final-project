import { defineStore } from 'pinia'

// Owner: Whitney (Person 3)
// UC-28,29,31,33,34
export const useEvaluationStore = defineStore('evaluation', {
  state: () => ({
    myReport: null as any,
    sectionReport: null as any,
    studentReport: null as any,
  }),
  actions: {
    // TODO: submitEvaluation(data), fetchMyReport(weekId),
    //       fetchSectionReport(sectionId, weekId), fetchStudentEvalReport(studentId, start, end),
    //       fetchStudentWARReport(studentId, start, end)
  },
})
