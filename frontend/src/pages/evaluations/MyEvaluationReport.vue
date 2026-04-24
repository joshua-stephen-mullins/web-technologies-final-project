<template>
  <v-container class="pa-6" max-width="1100">
    <div class="d-flex align-center gap-3 mb-6">
      <v-btn icon="mdi-arrow-left" variant="text" to="/evaluations/submit" />
      <div>
        <h1 class="text-h5 font-weight-bold">My Peer Evaluation Report</h1>
        <p class="text-caption text-medium-emphasis">View how your teammates rated you</p>
      </div>
    </div>

    <v-alert v-if="error" type="error" class="mb-4">{{ error }}</v-alert>

    <!-- Week selector -->
    <v-card class="mb-4">
      <v-card-text class="pa-4">
        <div class="d-flex gap-4 align-end flex-wrap">
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
            no-data-text="No completed weeks available"
          />
          <v-btn color="primary" :loading="evalStore.loading" :disabled="!selectedWeekId" @click="generate">
            Generate Report
          </v-btn>
        </div>
      </v-card-text>
    </v-card>

    <!-- Report output -->
    <template v-if="generated && evalStore.myReport">
      <!-- 5a: No evaluations received -->
      <v-alert v-if="evalStore.myReport.evaluatorCount === 0" type="info" class="mb-4">
        No peer evaluations have been submitted for you this week. Check back later.
      </v-alert>

      <v-card v-else>
        <v-card-title class="pa-4 pb-2">
          Results — {{ evalStore.myReport.weekLabel }}
          <span class="text-caption text-medium-emphasis ml-3">
            (based on {{ evalStore.myReport.evaluatorCount }} evaluator{{ evalStore.myReport.evaluatorCount !== 1 ? 's' : '' }})
          </span>
        </v-card-title>
        <v-card-text class="pa-0">
          <div class="table-wrap">
            <table class="eval-table">
              <thead>
                <tr>
                  <th>Student</th>
                  <th
                    v-for="c in evalStore.myReport.criterionAverages"
                    :key="c.criterionName"
                    class="text-right"
                  >
                    <div>{{ c.criterionName }}</div>
                    <div class="th-desc">{{ c.criterionDescription }}</div>
                  </th>
                  <th>Public Comments</th>
                  <th class="text-right">Grade</th>
                </tr>
              </thead>
              <tbody>
                <tr class="data-row">
                  <td class="cell">{{ evalStore.myReport.studentName }}</td>
                  <td
                    v-for="c in evalStore.myReport.criterionAverages"
                    :key="c.criterionName"
                    class="cell text-right"
                  >
                    {{ c.averageScore.toFixed(2) }}
                    <span class="text-max">/ {{ c.maxScore }}</span>
                  </td>
                  <td class="cell comments-cell">
                    <div v-if="!evalStore.myReport.publicComments.length" class="text-muted">—</div>
                    <div
                      v-for="(comment, i) in evalStore.myReport.publicComments"
                      :key="i"
                      class="comment-item"
                    >
                      {{ comment }}
                    </div>
                  </td>
                  <td class="cell text-right grade-cell">
                    {{ evalStore.myReport.averageGrade.toFixed(2) }}
                    <span class="text-max">/ {{ evalStore.myReport.maxGrade }}</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </v-card-text>
      </v-card>
    </template>
  </v-container>
</template>

<script setup lang="ts">
// Owner: Whitney (Person 3) | Student only
// UC-29: Select week, show averaged criterion scores, public comments, overall grade
// Business Rule BR-5: Students CANNOT see private comments or who evaluated them
import { ref, onMounted } from 'vue'
import { useEvaluationStore } from '@/stores/evaluationStore'
import { evaluationApi } from '@/apis/evaluationApi'

const evalStore = useEvaluationStore()

const weeks = ref<{ id: number; label: string }[]>([])
const selectedWeekId = ref<number | null>(null)
const loadingWeeks = ref(false)
const generated = ref(false)
const error = ref('')

onMounted(async () => {
  loadingWeeks.value = true
  try {
    const res = await evaluationApi.getMyReportWeeks()
    weeks.value = res.data.data.map((w: any) => ({
      id: w.id,
      label: `Week ${w.weekNumber}: ${w.startDate} – ${w.endDate}`,
    }))
    if (weeks.value.length > 0) {
      selectedWeekId.value = weeks.value[0].id
    }
  } catch {
    error.value = 'Failed to load weeks.'
  } finally {
    loadingWeeks.value = false
  }
})

async function generate() {
  if (!selectedWeekId.value) return
  error.value = ''
  generated.value = false
  try {
    await evalStore.fetchMyReport(selectedWeekId.value)
    generated.value = true
  } catch {
    error.value = 'Failed to generate report.'
  }
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
  vertical-align: bottom;
}
.eval-table th.text-right { text-align: right; }
.th-desc {
  font-size: 10px;
  font-weight: 400;
  text-transform: none;
  letter-spacing: 0;
  color: rgba(255,255,255,0.25);
  margin-top: 2px;
}
.data-row { border-bottom: 1px solid rgba(255,255,255,0.04); }
.cell { padding: 12px 14px; font-size: 13px; color: #F5F5F7; vertical-align: top; }
.cell.text-right { text-align: right; }
.text-max { color: rgba(255,255,255,0.35); font-size: 11px; }
.grade-cell { font-weight: 600; font-size: 14px; }
.comments-cell { max-width: 300px; }
.comment-item {
  padding: 3px 0;
  white-space: pre-wrap;
  word-break: break-word;
  border-bottom: 1px solid rgba(255,255,255,0.05);
}
.comment-item:last-child { border-bottom: none; }
.text-muted { color: rgba(255,255,255,0.25); }
</style>
