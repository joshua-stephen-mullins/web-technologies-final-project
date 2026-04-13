import { defineStore } from 'pinia'

// Owner: Oscar (Person 2)
// UC-11,12,13,15,16,17
export const useStudentStore = defineStore('student', {
  state: () => ({
    students: [] as any[],
    currentStudent: null as any,
  }),
  actions: {
    // TODO: fetchStudents(query), fetchStudentById(id), deleteStudent(id), inviteStudents(emails, sectionId)
  },
})
