import { defineStore } from 'pinia'

// Owner: Josh (Person 1)
// UC-2,3,4,5,6
export const useSectionStore = defineStore('section', {
  state: () => ({
    sections: [] as any[],
    currentSection: null as any,
    weeks: [] as any[],
  }),
  actions: {
    // TODO: fetchSections(query), fetchSectionById(id), createSection(data),
    //       updateSection(id, data), fetchWeeks(sectionId), updateActiveWeeks(sectionId, data)
  },
})
