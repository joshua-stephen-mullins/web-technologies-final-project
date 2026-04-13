import { defineStore } from 'pinia'

// Owner: Josh (Person 1)
// UC-1: Create rubric
export const useRubricStore = defineStore('rubric', {
  state: () => ({
    rubrics: [] as any[],
    currentRubric: null as any,
  }),
  actions: {
    // TODO: fetchRubrics(), fetchRubricById(id), createRubric(data), updateRubric(id, data)
  },
})
