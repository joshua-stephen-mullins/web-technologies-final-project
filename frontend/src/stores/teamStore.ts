import { defineStore } from 'pinia'
import { teamApi } from '@/apis/teamApi'

// Owner: Oscar (Person 2)
// UC-7,8,9,10,12,13,14
export const useTeamStore = defineStore('team', {
  state: () => ({
    teams: [] as any[],
    currentTeam: null as any,
    loading: false,
  }),
  actions: {
    async fetchTeams(params?: { name?: string; sectionId?: number }) {
      this.loading = true
      try {
        const res = await teamApi.search(params)
        this.teams = res.data.data
      } finally {
        this.loading = false
      }
    },
    async fetchTeamById(id: number) {
      this.loading = true
      try {
        const res = await teamApi.getById(id)
        this.currentTeam = res.data.data
      } finally {
        this.loading = false
      }
    },
    async createTeam(data: { name: string; description?: string; websiteUrl?: string; sectionId: number }) {
      const res = await teamApi.create(data)
      this.teams.push(res.data.data)
      return res.data.data
    },
    async updateTeam(id: number, data: { name: string; description?: string; websiteUrl?: string; sectionId?: number }) {
      const res = await teamApi.update(id, data)
      const idx = this.teams.findIndex((t) => t.id === id)
      if (idx !== -1) this.teams[idx] = res.data.data
      this.currentTeam = res.data.data
      return res.data.data
    },
    async deleteTeam(id: number) {
      await teamApi.remove(id)
      this.teams = this.teams.filter((t) => t.id !== id)
      if (this.currentTeam?.id === id) this.currentTeam = null
    },
    async assignStudents(teamId: number, studentIds: number[]) {
      const res = await teamApi.assignStudents(teamId, studentIds)
      this.currentTeam = res.data.data
      return res.data.data
    },
    async removeStudent(teamId: number, studentId: number) {
      const res = await teamApi.removeStudent(teamId, studentId)
      this.currentTeam = res.data.data
      return res.data.data
    },
  },
})
