<template>
  <div class="submit-page">
    <div class="page-header">
      <h1 class="page-title">Submit Peer Evaluations</h1>
    </div>

    <div v-if="loading" class="empty-msg">Loading...</div>
    <div v-else-if="loadError" class="error-msg">{{ loadError }}</div>

    <template v-else-if="form">
      <!-- Week Banner -->
      <div class="week-banner">
        <span class="week-label">Week {{ form.week.weekNumber }}</span>
        <span class="week-dates">{{ formatDate(form.week.startDate) }} – {{ formatDate(form.week.endDate) }}</span>
      </div>

      <div v-if="form.teammates.length === 0" class="empty-msg">
        No teammates found. Make sure you are assigned to a team.
      </div>
      <div v-else-if="form.criteria.length === 0" class="empty-msg">
        No rubric criteria found for your section.
      </div>

      <template v-else>
        <!-- One card per teammate -->
        <div
          v-for="teammate in form.teammates"
          :key="teammate.id"
          class="card teammate-card"
        >
          <h2 class="teammate-name">{{ teammate.firstName }} {{ teammate.lastName }}</h2>

          <!-- Score grid -->
          <div class="criteria-list">
            <div v-for="criterion in form.criteria" :key="criterion.id" class="criterion-row">
              <div class="criterion-info">
                <span class="criterion-name">{{ criterion.name }}</span>
                <span class="criterion-desc">{{ criterion.description }}</span>
              </div>
              <div class="score-input-wrap">
                <input
                  v-model.number="scores[teammate.id][criterion.id]"
                  type="number"
                  :min="1"
                  :max="criterion.maxScore"
                  class="score-input"
                  :placeholder="`1–${criterion.maxScore}`"
                />
                <span class="score-max">/ {{ criterion.maxScore }}</span>
              </div>
            </div>
          </div>

          <!-- Comments -->
          <div class="comments-section">
            <div class="field-group">
              <label class="field-label">Public Comments</label>
              <textarea
                v-model="publicComments[teammate.id]"
                class="comment-input"
                rows="2"
                placeholder="Visible to the teammate…"
              />
            </div>
            <div class="field-group">
              <label class="field-label">Private Comments</label>
              <textarea
                v-model="privateComments[teammate.id]"
                class="comment-input"
                rows="2"
                placeholder="Visible to instructors only…"
              />
            </div>
          </div>
        </div>

        <div v-if="submitError" class="error-msg">{{ submitError }}</div>
        <div v-if="successMsg" class="success-msg">{{ successMsg }}</div>

        <div class="submit-row">
          <v-btn
            color="primary"
            rounded="xl"
            size="large"
            :loading="submitting"
            :disabled="!allScoresFilled"
            @click="submit"
          >
            Submit Evaluations
          </v-btn>
        </div>
      </template>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { evaluationApi } from '@/apis/evaluationApi'

// Owner: Whitney (Person 3) | Student only
// UC-28: Submit peer evaluations for the previous week

interface WeekInfo { id: number; weekNumber: number; startDate: string; endDate: string }
interface TeammateInfo { id: number; firstName: string; lastName: string }
interface CriterionInfo { id: number; name: string; description: string; maxScore: number }
interface ExistingEvaluation { publicComments: string; privateComments: string; scores: Record<number, number> }
interface SubmitForm {
  week: WeekInfo
  teammates: TeammateInfo[]
  criteria: CriterionInfo[]
  existingEvaluations: Record<number, ExistingEvaluation>
}

const loading = ref(true)
const loadError = ref('')
const form = ref<SubmitForm | null>(null)
const submitting = ref(false)
const submitError = ref('')
const successMsg = ref('')

const scores = reactive<Record<number, Record<number, number | null>>>({})
const publicComments = reactive<Record<number, string>>({})
const privateComments = reactive<Record<number, string>>({})

onMounted(async () => {
  try {
    const res = await evaluationApi.getSubmitForm()
    const data: SubmitForm = res.data.data
    form.value = data

    for (const teammate of data.teammates) {
      scores[teammate.id] = {}
      publicComments[teammate.id] = ''
      privateComments[teammate.id] = ''

      for (const criterion of data.criteria) {
        scores[teammate.id][criterion.id] = null
      }

      const existing = data.existingEvaluations[teammate.id]
      if (existing) {
        publicComments[teammate.id] = existing.publicComments ?? ''
        privateComments[teammate.id] = existing.privateComments ?? ''
        for (const [cId, score] of Object.entries(existing.scores)) {
          scores[teammate.id][Number(cId)] = score
        }
      }
    }
  } catch (e: any) {
    loadError.value = e?.response?.data?.message ?? 'Could not load evaluation form.'
  } finally {
    loading.value = false
  }
})

const allScoresFilled = computed(() => {
  if (!form.value) return false
  return form.value.teammates.every(t =>
    form.value!.criteria.every(c => {
      const v = scores[t.id]?.[c.id]
      return v !== null && v !== undefined && v >= 1 && v <= c.maxScore
    })
  )
})

function formatDate(d: string) {
  return new Date(d).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
}

async function submit() {
  if (!form.value) return
  submitting.value = true
  submitError.value = ''
  successMsg.value = ''

  try {
    const evaluations = form.value.teammates.map(t => ({
      evaluateeId: t.id,
      scores: Object.fromEntries(
        Object.entries(scores[t.id]).map(([k, v]) => [Number(k), Number(v)])
      ),
      publicComments: publicComments[t.id] ?? '',
      privateComments: privateComments[t.id] ?? '',
    }))

    await evaluationApi.submitBatch({ weekId: form.value.week.id, evaluations })
    successMsg.value = 'Evaluations submitted successfully.'
  } catch (e: any) {
    submitError.value = e?.response?.data?.message ?? 'Submission failed. Please try again.'
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.submit-page {
  padding: 40px 32px 80px;
  max-width: 760px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #F5F5F7;
  margin: 0;
}

.week-banner {
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 10px;
  padding: 12px 16px;
  margin-bottom: 20px;
}

.week-label {
  font-size: 13px;
  font-weight: 600;
  color: #F5F5F7;
}

.week-dates {
  font-size: 13px;
  color: rgba(255,255,255,0.4);
}

.card {
  background: #161618;
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 16px;
}

.teammate-name {
  font-size: 16px;
  font-weight: 600;
  color: #F5F5F7;
  margin: 0 0 20px;
}

.criteria-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.criterion-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  padding: 12px 14px;
  background: rgba(255,255,255,0.03);
  border-radius: 10px;
  border: 1px solid rgba(255,255,255,0.05);
}

.criterion-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  flex: 1;
}

.criterion-name {
  font-size: 14px;
  font-weight: 500;
  color: #F5F5F7;
}

.criterion-desc {
  font-size: 12px;
  color: rgba(255,255,255,0.35);
}

.score-input-wrap {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
}

.score-input {
  width: 64px;
  background: rgba(255,255,255,0.07);
  border: 1px solid rgba(255,255,255,0.12);
  border-radius: 8px;
  padding: 6px 10px;
  font-size: 14px;
  color: #F5F5F7;
  text-align: center;
  outline: none;
  font-family: inherit;
}

.score-input:focus {
  border-color: rgba(255,255,255,0.3);
}

.score-max {
  font-size: 13px;
  color: rgba(255,255,255,0.35);
}

.comments-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid rgba(255,255,255,0.06);
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-label {
  font-size: 11px;
  font-weight: 600;
  color: rgba(255,255,255,0.4);
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.comment-input {
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 13px;
  color: #F5F5F7;
  outline: none;
  resize: vertical;
  font-family: inherit;
  transition: border-color 0.15s;
}

.comment-input:focus {
  border-color: rgba(255,255,255,0.25);
}

.submit-row {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
}

.empty-msg {
  font-size: 13px;
  color: rgba(255,255,255,0.25);
  padding: 20px 0;
  text-align: center;
}

.error-msg {
  font-size: 13px;
  color: #FF453A;
  padding: 10px 14px;
  background: rgba(255,69,58,0.08);
  border-radius: 8px;
  margin-bottom: 12px;
}

.success-msg {
  font-size: 13px;
  color: #30D158;
  padding: 10px 14px;
  background: rgba(48,209,88,0.08);
  border-radius: 8px;
  margin-bottom: 12px;
}
</style>
