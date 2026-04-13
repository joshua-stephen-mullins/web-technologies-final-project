import { defineStore } from 'pinia'

// Owner: Oscar (Person 2)
// UC-27 (manage WAR), UC-32 (team WAR report)
export const useActivityStore = defineStore('activity', {
  state: () => ({
    activities: [] as any[],
    warReport: null as any,
  }),
  actions: {
    // TODO: fetchActivities(studentId, weekId), createActivity(data),
    //       updateActivity(id, data), deleteActivity(id), fetchWARReport(teamId, weekId)
  },
})
