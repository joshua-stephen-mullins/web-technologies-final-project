import { defineStore } from 'pinia'
import { sectionApi } from '@/apis/sectionApi'

export const useSectionStore = defineStore('section', {
  state: () => ({
    sections: [] as any[],
    currentSection: null as any,
    weeks: [] as any[],
    loading: false,
  }),
  actions: {
    async fetchSections(name?: string) {
      this.loading = true
      try {
        const res = await sectionApi.search(name)
        this.sections = res.data.data
      } finally {
        this.loading = false
      }
    },
    async fetchSectionById(id: number) {
      this.loading = true
      try {
        const res = await sectionApi.getById(id)
        this.currentSection = res.data.data
      } finally {
        this.loading = false
      }
    },
    async createSection(data: { name: string; startDate: string; endDate: string; rubricId?: number }) {
      const res = await sectionApi.create(data)
      this.sections.push(res.data.data)
      return res.data.data
    },
    async updateSection(id: number, data: { name: string; startDate: string; endDate: string; rubricId?: number }) {
      const res = await sectionApi.update(id, data)
      const idx = this.sections.findIndex((s) => s.id === id)
      if (idx !== -1) this.sections[idx] = res.data.data
      this.currentSection = res.data.data
      return res.data.data
    },
    async fetchWeeks(sectionId: number) {
      const res = await sectionApi.getWeeks(sectionId)
      this.weeks = res.data.data
    },
    async updateActiveWeeks(sectionId: number, inactiveWeekNumbers: number[]) {
      const res = await sectionApi.updateActiveWeeks(sectionId, inactiveWeekNumbers)
      this.weeks = res.data.data
    },
  },
})
