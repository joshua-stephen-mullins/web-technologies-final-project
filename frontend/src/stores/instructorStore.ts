import { defineStore } from 'pinia'

// Owner: Whitey (Person 3)
// UC-18,19,20,21,22,23,24
export const useInstructorStore = defineStore('instructor', {
  state: () => ({
    instructors: [] as any[],
    currentInstructor: null as any,
  }),
  actions: {
    // TODO: fetchInstructors(query), fetchInstructorById(id),
    //       deactivateInstructor(id), reactivateInstructor(id),
    //       assignInstructor(teamId, instructorId), removeInstructor(teamId, instructorId),
    //       inviteInstructors(emails)
  },
})
