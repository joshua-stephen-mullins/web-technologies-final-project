import { defineStore } from 'pinia'
import { activityApi } from '@/apis/activityApi'

// Owner: Oscar (Person 2)
// UC-27 (manage WAR), UC-32 (team WAR report)
export const useActivityStore = defineStore('activity', {
  state: () => ({
    activities: [] as any[],
    warReport: [] as any[],
    studentWARReport: [] as any[],
    loading: false,
  }),
  actions: {
    async fetchActivities(weekId: number) {
      this.loading = true
      try {
        const res = await activityApi.getByWeek(weekId)
        this.activities = res.data.data
      } finally {
        this.loading = false
      }
    },
    async createActivity(data: { weekId: number; category: string; description: string; plannedHours: number; actualHours?: number; status: string }) {
      const res = await activityApi.create(data)
      this.activities.push(res.data.data)
      return res.data.data
    },
    async updateActivity(id: number, data: { weekId?: number; category: string; description: string; plannedHours: number; actualHours?: number; status: string }) {
      const res = await activityApi.update(id, data)
      const idx = this.activities.findIndex((a) => a.id === id)
      if (idx !== -1) this.activities[idx] = res.data.data
      return res.data.data
    },
    async deleteActivity(id: number) {
      await activityApi.remove(id)
      this.activities = this.activities.filter((a) => a.id !== id)
    },
    async fetchWARReport(teamId: number, weekId: number) {
      this.loading = true
      try {
        const res = await activityApi.getTeamWARReport(teamId, weekId)
        this.warReport = res.data.data
      } finally {
        this.loading = false
      }
    },
    async fetchStudentWARReport(studentId: number, weekIds: number[]) {
      this.loading = true
      try {
        const res = await activityApi.getStudentWARReport(studentId, weekIds)
        this.studentWARReport = res.data.data
      } finally {
        this.loading = false
      }
    },
  },
})
