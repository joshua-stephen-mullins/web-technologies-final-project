import { defineStore } from 'pinia'

// Owner: Oscar (Person 2)
// UC-7,8,9,10,12,13,14
export const useTeamStore = defineStore('team', {
  state: () => ({
    teams: [] as any[],
    currentTeam: null as any,
  }),
  actions: {
    // TODO: fetchTeams(query), fetchTeamById(id), createTeam(data), updateTeam(id, data),
    //       deleteTeam(id), assignStudents(teamId, studentIds), removeStudent(teamId, studentId)
  },
})
