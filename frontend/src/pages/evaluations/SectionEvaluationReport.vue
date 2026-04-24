<template>
  <v-container class="pa-6" max-width="1200">
    <div class="d-flex align-center gap-3 mb-6">
      <v-btn icon="mdi-arrow-left" variant="text" to="/" />
      <div>
        <h1 class="text-h5 font-weight-bold">Section Peer Evaluation Report</h1>
        <p class="text-caption text-medium-emphasis">View peer evaluation grades and comments for your section</p>
      </div>
    </div>

    <v-alert v-if="error" type="error" class="mb-4">{{ error }}</v-alert>

    <!-- Filters -->
    <v-card class="mb-4">
      <v-card-text class="pa-4">
        <div class="d-flex gap-4 align-end flex-wrap">
          <v-select
            v-model="selectedSectionId"
            :items="sections"
            item-title="name"
            item-value="id"
            label="Section"
            variant="outlined"
            density="compact"
            style="min-width: 220px"
            :loading="loadingSections"
            no-data-text="No sections assigned"
          />
          <v-select
            v-model="selectedWeekId"
            :items="weeks"
            item-title="label"
            item-value="id"
            label="Week"
            variant="outlined"
            density="compact"
            style="min-width: 280px"
            :loading="loadingWeeks"
            no-data-text="Select a section first"
          />
          <v-btn color="primary" :loading="evalStore.loading" :disabled="!selectedSectionId || !selectedWeekId" @click="generate">
            Generate Report
          </v-btn>
        </div>
      </v-card-text>
    </v-card>

    <!-- Report -->
    <template v-if="generated && evalStore.sectionReport">
      <!-- 5a: no students -->
      <v-alert v-if="!evalStore.sectionReport.students.length" type="info" class="mb-4">
        No students found in this section for the selected week.
      </v-alert>

      <template v-else>
        <!-- Non-submitters -->
        <v-alert v-if="evalStore.sectionReport.nonSubmitters.length" type="warning" variant="tonal" class="mb-4">
          <strong>Did not submit peer evaluation:</strong>
          {{ evalStore.sectionReport.nonSubmitters.join(', ') }}
        </v-alert>

        <v-card>
          <v-card-title class="pa-4 pb-2">
            Results — {{ evalStore.sectionReport.weekLabel }}
            <span class="text-caption text-medium-emphasis ml-3">{{ evalStore.sectionReport.students.length }} student(s)</span>
          </v-card-title>
          <v-card-text class="pa-0">
            <div class="table-wrap">
              <table class="eval-table">
                <thead>
                  <tr>
                    <th style="width:32px"></th>
                    <th>Student</th>
                    <th class="text-right">Grade</th>
                    <th>Commented by</th>
                    <th>Public Comments</th>
                    <th>Private Comments</th>
                  </tr>
                </thead>
                <tbody>
                  <template v-for="student in evalStore.sectionReport.students" :key="student.studentId">
                    <!-- No evaluations received -->
                    <template v-if="student.evaluations.length === 0">
                      <tr class="data-row">
                        <td class="cell icon-cell"></td>
                        <td class="cell name-cell">{{ student.studentName }}</td>
                        <td class="cell text-right muted">—</td>
                        <td class="cell muted" colspan="3">No evaluations received</td>
                      </tr>
                    </template>

                    <!-- One row per evaluator -->
                    <template v-else>
                      <tr v-for="(ev, idx) in student.evaluations" :key="idx" class="data-row">
                        <td class="cell icon-cell">
                          <button v-if="idx === 0" class="expand-btn" @click="toggle(student.studentId)" :title="expanded.has(student.studentId) ? 'Collapse details' : 'Expand criterion scores'">
                            <v-icon size="16" color="rgba(255,255,255,0.4)">
                              {{ expanded.has(student.studentId) ? 'mdi-chevron-up' : 'mdi-chevron-down' }}
                            </v-icon>
                          </button>
                        </td>
                        <td class="cell name-cell">{{ idx === 0 ? student.studentName : '' }}</td>
                        <td class="cell text-right grade-cell">
                          <template v-if="idx === 0">
                            {{ student.grade.toFixed(2) }}
                            <span class="text-max">/ {{ student.maxGrade }}</span>
                          </template>
                        </td>
                        <td class="cell">{{ ev.evaluatorName }}</td>
                        <td class="cell comment-cell">{{ ev.publicComments || '—' }}</td>
                        <td class="cell comment-cell private">{{ ev.privateComments || '—' }}</td>
                      </tr>
                    </template>

                    <!-- Expandable criterion scores detail -->
                    <tr v-if="expanded.has(student.studentId) && student.evaluations.length > 0" class="detail-row">
                      <td colspan="6" class="detail-cell">
                        <div class="detail-wrap">
                          <p class="detail-heading">Criterion Scores — {{ student.studentName }}</p>
                          <table class="detail-table">
                            <thead>
                              <tr>
                                <th>Evaluator</th>
                                <th
                                  v-for="c in student.evaluations[0].criterionScores"
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
                              <tr v-for="ev in student.evaluations" :key="ev.evaluatorName">
                                <td>{{ ev.evaluatorName }}</td>
                                <td v-for="c in ev.criterionScores" :key="c.criterionName" class="text-right">
                                  {{ c.score }}<span class="text-max"> / {{ c.maxScore }}</span>
                                </td>
                                <td class="text-right font-weight-medium">
                                  {{ evalTotal(ev) }}<span class="text-max"> / {{ student.maxGrade }}</span>
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
    </template>
  </v-container>
</template>

<script setup lang="ts">
// Owner: Whitney (Person 3) | Instructor only
// UC-31: Select section + week, show table of all students with grades + comments (public + private)
// Shows who did NOT submit their peer eval for that week
// Grade algorithm: average of all teammate total scores (per UC-31 spec)
import { ref, onMounted, watch } from 'vue'
import { useEvaluationStore } from '@/stores/evaluationStore'
import { evaluationApi } from '@/apis/evaluationApi'

const evalStore = useEvaluationStore()

const sections = ref<{ id: number; name: string }[]>([])
const weeks = ref<{ id: number; label: string }[]>([])
const selectedSectionId = ref<number | null>(null)
const selectedWeekId = ref<number | null>(null)
const loadingSections = ref(false)
const loadingWeeks = ref(false)
const generated = ref(false)
const error = ref('')
const expanded = ref<Set<number>>(new Set())

onMounted(async () => {
  loadingSections.value = true
  try {
    const res = await evaluationApi.getSectionReportSections()
    sections.value = res.data.data
  } catch {
    error.value = 'Failed to load sections.'
  } finally {
    loadingSections.value = false
  }
})

watch(selectedSectionId, async (sectionId) => {
  selectedWeekId.value = null
  weeks.value = []
  generated.value = false
  if (!sectionId) return
  loadingWeeks.value = true
  try {
    const res = await evaluationApi.getSectionReportWeeks(sectionId)
    weeks.value = res.data.data.map((w: any) => ({
      id: w.id,
      label: `Week ${w.weekNumber}: ${w.startDate} – ${w.endDate}`,
    }))
    if (weeks.value.length > 0) selectedWeekId.value = weeks.value[0].id
  } catch {
    error.value = 'Failed to load weeks.'
  } finally {
    loadingWeeks.value = false
  }
})

async function generate() {
  if (!selectedSectionId.value || !selectedWeekId.value) return
  error.value = ''
  generated.value = false
  expanded.value = new Set()
  try {
    await evalStore.fetchSectionReport(selectedSectionId.value, selectedWeekId.value)
    generated.value = true
  } catch {
    error.value = 'Failed to generate report.'
  }
}

function toggle(studentId: number) {
  const next = new Set(expanded.value)
  if (next.has(studentId)) next.delete(studentId)
  else next.add(studentId)
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
.name-cell { font-weight: 500; min-width: 140px; }
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
