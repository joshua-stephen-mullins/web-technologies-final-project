<template>
  <v-container class="pa-6" max-width="1000">
    <div class="d-flex align-center gap-3 mb-6">
      <v-btn icon="mdi-arrow-left" variant="text" to="/activities" />
      <div>
        <h1 class="text-h5 font-weight-bold">Team WAR Report</h1>
        <p class="text-caption text-medium-emphasis">View all team activities for a given week</p>
      </div>
    </div>

    <v-alert v-if="error" type="error" class="mb-4">{{ error }}</v-alert>

    <!-- Filters -->
    <v-card class="mb-4">
      <v-card-text class="pa-4">
        <div class="d-flex gap-4 align-end flex-wrap">
          <v-select
            v-model="selectedTeamId"
            :items="teams"
            item-title="name"
            item-value="id"
            label="Team"
            variant="outlined"
            density="compact"
            style="min-width: 220px"
            :loading="loadingTeams"
          />
          <v-select
            v-model="selectedWeekId"
            :items="weeks"
            item-title="label"
            item-value="id"
            label="Week"
            variant="outlined"
            density="compact"
            style="min-width: 260px"
            :loading="loadingWeeks"
          />
          <v-btn color="primary" :loading="activityStore.loading" :disabled="!selectedTeamId || !selectedWeekId"
                 @click="generate">
            Generate Report
          </v-btn>
        </div>
      </v-card-text>
    </v-card>

    <!-- Report -->
    <template v-if="generated">
      <v-card>
        <v-card-title class="pa-4 pb-2">
          Results — {{ groupedReport.length }} student(s)
        </v-card-title>
        <v-card-text class="pa-0">
          <div v-if="!activityStore.warReport.length" class="pa-6 text-center text-caption text-medium-emphasis">
            No activities found for this team/week.
          </div>
          <div v-else class="table-wrap">
            <table class="war-table">
              <thead>
                <tr>
                  <th>Student</th>
                  <th>Category</th>
                  <th>Description</th>
                  <th class="text-right">Planned</th>
                  <th class="text-right">Actual</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                <template v-for="group in groupedReport" :key="group.studentId">
                  <tr v-for="(act, idx) in group.activities" :key="act.id" class="data-row">
                    <td class="cell">{{ idx === 0 ? group.studentName : '' }}</td>
                    <td class="cell"><span class="chip">{{ act.category }}</span></td>
                    <td class="cell desc">{{ act.description }}</td>
                    <td class="cell text-right">{{ act.plannedHours }}</td>
                    <td class="cell text-right">{{ act.actualHours ?? '—' }}</td>
                    <td class="cell">{{ formatStatus(act.status) }}</td>
                  </tr>
                </template>
              </tbody>
            </table>
          </div>
        </v-card-text>
      </v-card>
    </template>
  </v-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useActivityStore } from '@/stores/activityStore'
import { useTeamStore } from '@/stores/teamStore'
import { sectionApi } from '@/apis/sectionApi'

const activityStore = useActivityStore()
const teamStore = useTeamStore()

const selectedTeamId = ref<number | null>(null)
const selectedWeekId = ref<number | null>(null)
const teams = computed(() => teamStore.teams)
const weeks = ref<any[]>([])
const loadingTeams = ref(false)
const loadingWeeks = ref(false)
const generated = ref(false)
const error = ref('')

const groupedReport = computed(() => {
  const map = new Map<number, { studentId: number; studentName: string; activities: any[] }>()
  for (const act of activityStore.warReport) {
    if (!map.has(act.studentId)) {
      map.set(act.studentId, { studentId: act.studentId, studentName: act.studentName, activities: [] })
    }
    map.get(act.studentId)!.activities.push(act)
  }
  return Array.from(map.values())
})

onMounted(async () => {
  loadingTeams.value = true
  try {
    await teamStore.fetchTeams()
  } finally {
    loadingTeams.value = false
  }
})

watch(selectedTeamId, async (teamId) => {
  selectedWeekId.value = null
  weeks.value = []
  if (!teamId) return
  const team = teams.value.find((t: any) => t.id === teamId)
  if (!team?.sectionId) return
  loadingWeeks.value = true
  try {
    const res = await sectionApi.getWeeks(team.sectionId)
    weeks.value = res.data.data
      .filter((w: any) => w.isActive)
      .map((w: any) => ({ id: w.id, label: `Week ${w.weekNumber}: ${w.startDate} – ${w.endDate}` }))
      .reverse()
  } finally {
    loadingWeeks.value = false
  }
})

async function generate() {
  if (!selectedTeamId.value || !selectedWeekId.value) return
  error.value = ''
  generated.value = false
  try {
    await activityStore.fetchWARReport(selectedTeamId.value, selectedWeekId.value)
    generated.value = true
  } catch {
    error.value = 'Failed to generate report.'
  }
}

function formatStatus(s: string) {
  return s === 'IN_PROGRESS' ? 'In Progress' : s === 'UNDER_TESTING' ? 'Under Testing' : s === 'DONE' ? 'Done' : s
}
</script>

<style scoped>
.table-wrap { overflow-x: auto; }
.war-table { width: 100%; border-collapse: collapse; }
.war-table th {
  padding: 10px 14px;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: rgba(255,255,255,0.4);
  text-align: left;
  border-bottom: 1px solid rgba(255,255,255,0.08);
}
.war-table th.text-right { text-align: right; }
.data-row { border-bottom: 1px solid rgba(255,255,255,0.04); }
.data-row:last-child { border-bottom: none; }
.cell { padding: 10px 14px; font-size: 13px; color: #F5F5F7; vertical-align: top; }
.cell.text-right { text-align: right; }
.desc { max-width: 300px; white-space: pre-wrap; word-break: break-word; }
.chip { display: inline-block; font-size: 11px; font-weight: 500; padding: 2px 8px; border-radius: 20px; background: rgba(255,255,255,0.08); color: rgba(255,255,255,0.6); white-space: nowrap; }
</style>
