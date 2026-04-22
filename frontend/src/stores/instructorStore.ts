import { defineStore } from 'pinia'
import { instructorApi } from '@/apis/instructorApi'
import { teamApi } from '@/apis/teamApi'

// Owner: Whitney (Person 3)
export const useInstructorStore = defineStore('instructor', {
  state: () => ({
    instructors: [] as any[],
    currentInstructor: null as any,
    teams: [] as any[],
    loading: false,
  }),
  actions: {
    async inviteInstructors(emails: string[]) {
      await instructorApi.invite(emails)
    },
    async fetchInstructors(params?: { firstName?: string; lastName?: string; teamName?: string; status?: string }) {
      this.loading = true
      try {
        const res = await instructorApi.listAll(params)
        this.instructors = res.data.data
      } finally {
        this.loading = false
      }
    },
    async fetchTeams() {
      const res = await teamApi.listAll()
      this.teams = res.data.data
    },
    async fetchTeamInstructors(teamId: number): Promise<any[]> {
      const res = await instructorApi.getTeamInstructors(teamId)
      return res.data.data
    },
    async assignInstructorsToTeam(teamId: number, instructorIds: number[]): Promise<any[]> {
      const res = await instructorApi.assignToTeam(teamId, instructorIds)
      return res.data.data
    },
    async removeInstructorFromTeam(teamId: number, instructorId: number) {
      await instructorApi.removeFromTeam(teamId, instructorId)
    },
  },
})
