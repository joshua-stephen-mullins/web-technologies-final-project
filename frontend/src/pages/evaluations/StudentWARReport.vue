<template>
  <v-container class="pa-6" max-width="1100">
    <div class="d-flex align-center gap-3 mb-6">
      <v-btn icon="mdi-arrow-left" variant="text" :to="`/students/${studentId}`" />
      <div>
        <h1 class="text-h5 font-weight-bold">
          WAR Report{{ studentName ? ' — ' + studentName : '' }}
        </h1>
        <p class="text-caption text-medium-emphasis">View weekly activity reports for this student</p>
      </div>
    </div>

    <v-alert v-if="error" type="error" class="mb-4">{{ error }}</v-alert>

    <!-- Week range selectors -->
    <v-card class="mb-4">
      <v-card-text class="pa-4">
        <div class="d-flex gap-4 align-end flex-wrap">
          <v-select
            v-model="startWeekId"
            :items="weeks"
            item-title="label"
            item-value="id"
            label="Start Week"
            variant="outlined"
            density="compact"
            style="min-width: 260px"
            :loading="loadingWeeks"
            no-data-text="No completed weeks available"
          />
          <v-select
            v-model="endWeekId"
            :items="weeks"
            item-title="label"
            item-value="id"
            label="End Week"
            variant="outlined"
            density="compact"
            style="min-width: 260px"
            :loading="loadingWeeks"
            no-data-text="No completed weeks available"
          />
          <v-btn color="primary" :loading="activityStore.loading" :disabled="!startWeekId || !endWeekId" @click="generate">
            Generate Report
          </v-btn>
        </div>
        <p v-if="rangeError" class="text-caption text-error mt-2">{{ rangeError }}</p>
      </v-card-text>
    </v-card>

    <!-- Report -->
    <template v-if="generated">
      <!-- 7a: no data -->
      <v-alert v-if="!activityStore.studentWARReport.length" type="info" class="mb-4">
        No activities found for this student in the selected week range.
      </v-alert>

      <template v-else>
        <div v-for="weekGroup in groupedByWeek" :key="weekGroup.weekId" class="mb-4">
          <div class="week-header">Active week: {{ weekGroup.weekLabel }}</div>
          <v-card>
            <v-card-text class="pa-0">
              <div class="table-wrap">
                <table class="war-table">
                  <thead>
                    <tr>
                      <th>Activity Category</th>
                      <th>Description</th>
                      <th class="text-right">Planned hrs</th>
                      <th class="text-right">Actual hrs</th>
                      <th>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="act in weekGroup.activities" :key="act.id" class="data-row">
                      <td class="cell"><span class="chip">{{ act.category }}</span></td>
                      <td class="cell desc">{{ act.description }}</td>
                      <td class="cell text-right">{{ act.plannedHours }}</td>
                      <td class="cell text-right">{{ act.actualHours ?? '—' }}</td>
                      <td class="cell">{{ formatStatus(act.status) }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </v-card-text>
          </v-card>
        </div>
      </template>
    </template>
  </v-container>
</template>

<script setup lang="ts">
// Owner: Whitney (Person 3) | Instructor only
// UC-34: Select start/end weeks, show all activities grouped by week for one student
// Accessed from StudentDetail.vue (UC-16)
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useActivityStore } from '@/stores/activityStore'
import { evaluationApi } from '@/apis/evaluationApi'
import { studentApi } from '@/apis/studentApi'

const route = useRoute()
const activityStore = useActivityStore()
const studentId = Number(route.params.id)

const studentName = ref('')
const weeks = ref<{ id: number; label: string; weekNumber: number }[]>([])
const startWeekId = ref<number | null>(null)
const endWeekId = ref<number | null>(null)
const loadingWeeks = ref(false)
const generated = ref(false)
const error = ref('')
const rangeError = ref('')

// Group activities by weekId, sorted chronologically, using week labels from our weeks list
const groupedByWeek = computed(() => {
  const map = new Map<number, { weekId: number; weekLabel: string; weekNumber: number; activities: any[] }>()
  for (const act of activityStore.studentWARReport) {
    if (!map.has(act.weekId)) {
      const w = weeks.value.find(w => w.id === act.weekId)
      map.set(act.weekId, {
        weekId: act.weekId,
        weekLabel: w?.label ?? `Week ${act.weekId}`,
        weekNumber: w?.weekNumber ?? 0,
        activities: [],
      })
    }
    map.get(act.weekId)!.activities.push(act)
  }
  return Array.from(map.values()).sort((a, b) => a.weekNumber - b.weekNumber)
})

onMounted(async () => {
  // Load student name for header
  try {
    const res = await studentApi.getById(studentId)
    const s = res.data.data
    studentName.value = s.firstName + ' ' + s.lastName
  } catch { /* non-critical */ }

  // Load weeks via UC-33 endpoint (completed weeks for student's section)
  loadingWeeks.value = true
  try {
    const res = await evaluationApi.getStudentReportWeeks(studentId)
    weeks.value = res.data.data.map((w: any) => ({
      id: w.id,
      weekNumber: w.weekNumber,
      label: `Week ${w.weekNumber}: ${w.startDate} – ${w.endDate}`,
    }))
    if (weeks.value.length > 0) {
      startWeekId.value = weeks.value[0].id
      endWeekId.value = weeks.value[weeks.value.length - 1].id
    }
  } catch {
    error.value = 'Failed to load weeks.'
  } finally {
    loadingWeeks.value = false
  }
})

async function generate() {
  if (!startWeekId.value || !endWeekId.value) return
  rangeError.value = ''

  const startNum = weeks.value.find(w => w.id === startWeekId.value)?.weekNumber ?? 0
  const endNum = weeks.value.find(w => w.id === endWeekId.value)?.weekNumber ?? 0
  if (startNum > endNum) {
    rangeError.value = 'Start week must be before or equal to end week.'
    return
  }

  const weekIds = weeks.value
    .filter(w => w.weekNumber >= startNum && w.weekNumber <= endNum)
    .map(w => w.id)

  error.value = ''
  generated.value = false
  try {
    await activityStore.fetchStudentWARReport(studentId, weekIds)
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
.week-header {
  font-size: 13px;
  font-weight: 600;
  color: rgba(255,255,255,0.55);
  padding: 4px 2px 8px;
  letter-spacing: 0.02em;
}
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
.desc { max-width: 400px; white-space: pre-wrap; word-break: break-word; }
.chip {
  display: inline-block;
  font-size: 11px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: 20px;
  background: rgba(255,255,255,0.08);
  color: rgba(255,255,255,0.6);
  white-space: nowrap;
}
</style>
