import { defineStore } from 'pinia'
import { rubricApi } from '@/apis/rubricApi'

export const useRubricStore = defineStore('rubric', {
  state: () => ({
    rubrics: [] as any[],
    currentRubric: null as any,
    loading: false,
  }),
  actions: {
    async fetchRubrics() {
      this.loading = true
      try {
        const res = await rubricApi.getAll()
        this.rubrics = res.data.data
      } finally {
        this.loading = false
      }
    },
    async fetchRubricById(id: number) {
      this.loading = true
      try {
        const res = await rubricApi.getById(id)
        this.currentRubric = res.data.data
      } finally {
        this.loading = false
      }
    },
    async createRubric(data: { name: string; criteria: any[] }) {
      const res = await rubricApi.create(data)
      this.rubrics.push(res.data.data)
      return res.data.data
    },
    async updateRubric(id: number, data: { name: string; criteria: any[] }) {
      const res = await rubricApi.update(id, data)
      const idx = this.rubrics.findIndex((r) => r.id === id)
      if (idx !== -1) this.rubrics[idx] = res.data.data
      this.currentRubric = res.data.data
      return res.data.data
    },
    async deleteRubric(id: number) {
      await rubricApi.remove(id)
      this.rubrics = this.rubrics.filter((r) => r.id !== id)
    },
  },
})
