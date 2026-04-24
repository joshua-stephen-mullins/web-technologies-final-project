<template>
  <v-container class="pa-6" max-width="1200">
    <div class="d-flex align-center gap-3 mb-6">
      <v-btn icon="mdi-arrow-left" variant="text" :to="`/students/${studentId}`" />
      <div>
        <h1 class="text-h5 font-weight-bold">
          Peer Evaluation Report{{ studentName ? ' — ' + studentName : '' }}
        </h1>
        <p class="text-caption text-medium-emphasis">View per-week grades and comments for this student</p>
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
          <v-btn color="primary" :loading="evalStore.loading" :disabled="!startWeekId || !endWeekId" @click="generate">
            Generate Report
          </v-btn>
        </div>
        <p v-if="rangeError" class="text-caption text-error mt-2">{{ rangeError }}</p>
      </v-card-text>
    </v-card>

    <!-- Report -->
    <template v-if="generated && evalStore.studentReport">
      <!-- 7a: no data -->
      <v-alert v-if="noData" type="info" class="mb-4">
        No peer evaluations found for this student in the selected week range.
      </v-alert>

      <v-card v-else>
        <v-card-title class="pa-4 pb-2">
          Results — {{ evalStore.studentReport.weeks.length }} week(s)
        </v-card-title>
        <v-card-text class="pa-0">
          <div class="table-wrap">
            <table class="eval-table">
              <thead>
                <tr>
                  <th style="width:32px"></th>
                  <th>Week</th>
                  <th class="text-right">Grade</th>
                  <th>Commented by</th>
                  <th>Public Comments</th>
                  <th>Private Comments</th>
                </tr>
              </thead>
              <tbody>
                <template v-for="week in evalStore.studentReport.weeks" :key="week.weekLabel">
                  <!-- No evaluations this week -->
                  <template v-if="week.evaluations.length === 0">
                    <tr class="data-row">
                      <td class="cell icon-cell"></td>
                      <td class="cell week-cell">{{ week.weekLabel }}</td>
                      <td class="cell text-right muted">—</td>
                      <td class="cell muted" colspan="3">No evaluations received</td>
                    </tr>
                  </template>

                  <!-- One row per evaluator -->
                  <template v-else>
                    <tr v-for="(ev, idx) in week.evaluations" :key="idx" class="data-row">
                      <td class="cell icon-cell">
                        <button v-if="idx === 0" class="expand-btn" @click="toggle(week.weekLabel)"
                                :title="expanded.has(week.weekLabel) ? 'Collapse details' : 'Expand criterion scores'">
                          <v-icon size="16" color="rgba(255,255,255,0.4)">
                            {{ expanded.has(week.weekLabel) ? 'mdi-chevron-up' : 'mdi-chevron-down' }}
                          </v-icon>
                        </button>
                      </td>
                      <td class="cell week-cell">{{ idx === 0 ? week.weekLabel : '' }}</td>
                      <td class="cell text-right grade-cell">
                        <template v-if="idx === 0">
                          {{ week.grade.toFixed(2) }}
                          <span class="text-max">/ {{ week.maxGrade }}</span>
                        </template>
                      </td>
                      <td class="cell">{{ ev.evaluatorName }}</td>
                      <td class="cell comment-cell">{{ ev.publicComments || '—' }}</td>
                      <td class="cell comment-cell private">{{ ev.privateComments || '—' }}</td>
                    </tr>
                  </template>

                  <!-- Expandable criterion scores -->
                  <tr v-if="expanded.has(week.weekLabel) && week.evaluations.length > 0" class="detail-row">
                    <td colspan="6" class="detail-cell">
                      <div class="detail-wrap">
                        <p class="detail-heading">Criterion Scores — {{ week.weekLabel }}</p>
                        <table class="detail-table">
                          <thead>
                            <tr>
                              <th>Evaluator</th>
                              <th
                                v-for="c in week.evaluations[0].criterionScores"
                                :key="c.criterionName"
                                class="text-right"
                                :title="c.criterionDescription"
                              >
                                {{ c.criterionName }}
                              </th>
                              <th class="text-right">Total</th>
                            </tr>
                          </thead>
                          <tbody>
                            <tr v-for="ev in week.evaluations" :key="ev.evaluatorName">
                              <td>{{ ev.evaluatorName }}</td>
                              <td v-for="c in ev.criterionScores" :key="c.criterionName" class="text-right">
                                {{ c.score }}<span class="text-max"> / {{ c.maxScore }}</span>
                              </td>
                              <td class="text-right font-weight-medium">
                                {{ evalTotal(ev) }}<span class="text-max"> / {{ week.maxGrade }}</span>
                              </td>
                            </tr>
                          </tbody>
                        </table>
                      </div>
                    </td>
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
// Owner: Whitney (Person 3) | Instructor only
// UC-33: Select start/end weeks, show per-week grade history for one student
// Accessed from StudentDetail.vue (UC-16)
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useEvaluationStore } from '@/stores/evaluationStore'
import { evaluationApi } from '@/apis/evaluationApi'
import { studentApi } from '@/apis/studentApi'

const route = useRoute()
const evalStore = useEvaluationStore()
const studentId = Number(route.params.id)

const studentName = ref('')
const weeks = ref<{ id: number; label: string; weekNumber: number }[]>([])
const startWeekId = ref<number | null>(null)
const endWeekId = ref<number | null>(null)
const loadingWeeks = ref(false)
const generated = ref(false)
const error = ref('')
const rangeError = ref('')
const expanded = ref<Set<string>>(new Set())

const noData = computed(() =>
  evalStore.studentReport?.weeks.every((w: any) => w.evaluations.length === 0)
)

onMounted(async () => {
  // Load student name for header
  try {
    const res = await studentApi.getById(studentId)
    const s = res.data.data
    studentName.value = s.firstName + ' ' + s.lastName
  } catch { /* non-critical */ }

  // Load weeks
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

  error.value = ''
  generated.value = false
  expanded.value = new Set()
  try {
    await evalStore.fetchStudentReport(studentId, startWeekId.value, endWeekId.value)
    generated.value = true
  } catch {
    error.value = 'Failed to generate report.'
  }
}

function toggle(key: string) {
  const next = new Set(expanded.value)
  if (next.has(key)) next.delete(key)
  else next.add(key)
  expanded.value = next
}

function evalTotal(ev: any): string {
  const total = ev.criterionScores.reduce((sum: number, c: any) => sum + c.score, 0)
  return total.toFixed(2)
}
</script>

<style scoped>
.table-wrap { overflow-x: auto; }
.eval-table { width: 100%; border-collapse: collapse; }
.eval-table th {
  padding: 10px 14px;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: rgba(255,255,255,0.4);
  text-align: left;
  border-bottom: 1px solid rgba(255,255,255,0.08);
}
.eval-table th.text-right { text-align: right; }
.data-row { border-bottom: 1px solid rgba(255,255,255,0.04); }
.data-row:last-child { border-bottom: none; }
.cell { padding: 10px 14px; font-size: 13px; color: #F5F5F7; vertical-align: top; }
.cell.text-right { text-align: right; }
.icon-cell { padding: 8px 6px; width: 32px; }
.week-cell { font-weight: 500; white-space: nowrap; min-width: 200px; }
.grade-cell { font-weight: 600; white-space: nowrap; }
.comment-cell { max-width: 240px; white-space: pre-wrap; word-break: break-word; }
.private { color: rgba(255, 220, 100, 0.85); }
.text-max { color: rgba(255,255,255,0.35); font-size: 11px; font-weight: 400; }
.muted { color: rgba(255,255,255,0.3); }

.expand-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 2px;
  display: flex;
  align-items: center;
  border-radius: 4px;
}
.expand-btn:hover { background: rgba(255,255,255,0.06); }

.detail-row { background: rgba(255,255,255,0.02); }
.detail-cell { padding: 0 !important; }
.detail-wrap { padding: 12px 20px 16px 48px; }
.detail-heading {
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: rgba(255,255,255,0.3);
  margin-bottom: 8px;
}
.detail-table { border-collapse: collapse; font-size: 12px; }
.detail-table th {
  padding: 6px 12px;
  font-size: 10px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: rgba(255,255,255,0.3);
  text-align: left;
  border-bottom: 1px solid rgba(255,255,255,0.06);
}
.detail-table th.text-right { text-align: right; }
.detail-table td {
  padding: 6px 12px;
  color: rgba(255,255,255,0.75);
  border-bottom: 1px solid rgba(255,255,255,0.03);
}
.detail-table td.text-right { text-align: right; }
.detail-table tr:last-child td { border-bottom: none; }
</style>
