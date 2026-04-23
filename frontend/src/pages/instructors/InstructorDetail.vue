<template>
  <div class="detail-page">
    <div class="page-header">
      <v-btn icon="mdi-arrow-left" variant="text" to="/instructors" />
      <h1 class="page-title">Instructor Details</h1>
    </div>

    <div v-if="loading" class="empty-msg">Loading...</div>
    <div v-else-if="loadError" class="error-msg">{{ loadError }}</div>

    <template v-else-if="instructor">
      <!-- Identity Card -->
      <div class="card">
        <div class="card-top">
          <div>
            <p class="instructor-name">{{ instructor.firstName }} {{ instructor.lastName }}</p>
            <p class="instructor-email">{{ instructor.email }}</p>
          </div>
          <span :class="instructor.enabled ? 'status-active' : 'status-deactivated'">
            {{ instructor.enabled ? 'Active' : 'Deactivated' }}
          </span>
        </div>

        <div class="card-actions">
          <v-btn
            v-if="instructor.enabled"
            variant="outlined"
            color="error"
            size="small"
            rounded="xl"
            @click="openDeactivate"
          >
            Deactivate
          </v-btn>
          <v-btn
            v-else
            variant="outlined"
            color="success"
            size="small"
            rounded="xl"
            @click="reactivateDialog = true"
          >
            Reactivate
          </v-btn>
        </div>
      </div>

      <!-- Supervised Teams -->
      <div class="card">
        <h2 class="section-heading">Supervised Teams</h2>
        <div v-if="instructor.teamsBySection.length === 0" class="empty-msg">
          Not assigned to any teams.
        </div>
        <div
          v-for="section in instructor.teamsBySection"
          :key="section.sectionId"
          class="section-group"
        >
          <p class="section-name">{{ section.sectionName }}</p>
          <div class="team-list">
            <div v-for="team in section.teams" :key="team.id" class="team-row">
              <span class="team-name">{{ team.name }}</span>
            </div>
          </div>
        </div>
      </div>

      <div v-if="successMsg" class="success-msg">{{ successMsg }}</div>
    </template>

    <!-- Reactivate Confirmation Dialog -->
    <v-dialog v-model="reactivateDialog" max-width="440">
      <div class="confirm-card">
        <h2 class="confirm-title">Reactivate Instructor</h2>
        <p class="confirm-body">
          Reactivate <strong>{{ instructor?.firstName }} {{ instructor?.lastName }}</strong>?
          They will regain access to the System and receive an email notification.
        </p>
        <div v-if="reactivateError" class="error-msg">{{ reactivateError }}</div>
        <div class="confirm-actions">
          <v-btn variant="text" @click="reactivateDialog = false">Cancel</v-btn>
          <v-btn color="success" rounded="xl" :loading="reactivating" @click="confirmReactivate">
            Confirm Reactivation
          </v-btn>
        </div>
      </div>
    </v-dialog>

    <!-- Deactivate Confirmation Dialog -->
    <v-dialog v-model="deactivateDialog" max-width="500">
      <div class="confirm-card">
        <h2 class="confirm-title">Deactivate Instructor</h2>

        <div class="consequence-box">
          <p class="consequence-label">Consequences of this action</p>
          <ul class="consequence-list">
            <li>This instructor will no longer have access to the System.</li>
            <li>The instructor's information is kept in the System.</li>
            <li>This account can be recovered in the future.</li>
          </ul>
        </div>

        <div class="field-group">
          <label class="field-label">Reason for deactivation</label>
          <textarea
            v-model="deactivateReason"
            class="reason-input"
            placeholder="Enter a reason..."
            rows="3"
          />
        </div>

        <p class="confirm-warning">
          Are you sure you want to deactivate
          <strong>{{ instructor?.firstName }} {{ instructor?.lastName }}</strong>?
          This will immediately revoke their access.
        </p>

        <div v-if="deactivateError" class="error-msg">{{ deactivateError }}</div>

        <div class="confirm-actions">
          <v-btn variant="text" @click="deactivateDialog = false">Cancel</v-btn>
          <v-btn
            color="error"
            rounded="xl"
            :loading="deactivating"
            :disabled="!deactivateReason.trim()"
            @click="confirmDeactivate"
          >
            Confirm Deactivation
          </v-btn>
        </div>
      </div>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { instructorApi } from '@/apis/instructorApi'

// Owner: Whitney (Person 3) | Admin only
// UC-22: view instructor details
// UC-23: deactivate instructor
// UC-24: reactivate button stubbed

interface TeamSummary { id: number; name: string }
interface SectionTeams { sectionId: number; sectionName: string; teams: TeamSummary[] }
interface InstructorDetail {
  id: number
  firstName: string
  lastName: string
  email: string
  enabled: boolean
  teamsBySection: SectionTeams[]
}

const route = useRoute()
const loading = ref(true)
const loadError = ref('')
const instructor = ref<InstructorDetail | null>(null)
const successMsg = ref('')

const deactivateDialog = ref(false)
const deactivateReason = ref('')
const deactivating = ref(false)
const deactivateError = ref('')

const reactivateDialog = ref(false)
const reactivating = ref(false)
const reactivateError = ref('')

onMounted(async () => {
  try {
    const res = await instructorApi.getById(Number(route.params.id))
    instructor.value = res.data.data
  } catch {
    loadError.value = 'Could not load instructor. Please go back and try again.'
  } finally {
    loading.value = false
  }
})

function openDeactivate() {
  deactivateReason.value = ''
  deactivateError.value = ''
  successMsg.value = ''
  deactivateDialog.value = true
}

async function confirmReactivate() {
  reactivating.value = true
  reactivateError.value = ''
  try {
    await instructorApi.reactivate(instructor.value!.id)
    instructor.value!.enabled = true
    reactivateDialog.value = false
    successMsg.value = `${instructor.value!.firstName} ${instructor.value!.lastName} has been reactivated. A notification email has been sent.`
  } catch (e: any) {
    reactivateError.value = e?.response?.data?.message ?? 'Reactivation failed. Please try again.'
  } finally {
    reactivating.value = false
  }
}

async function confirmDeactivate() {
  deactivating.value = true
  deactivateError.value = ''
  try {
    await instructorApi.deactivate(instructor.value!.id)
    instructor.value!.enabled = false
    deactivateDialog.value = false
    successMsg.value = `${instructor.value!.firstName} ${instructor.value!.lastName} has been deactivated.`
  } catch (e: any) {
    deactivateError.value = e?.response?.data?.message ?? 'Deactivation failed. Please try again.'
  } finally {
    deactivating.value = false
  }
}
</script>

<style scoped>
.detail-page {
  padding: 40px 32px 80px;
  max-width: 680px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 28px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #F5F5F7;
  margin: 0;
}

.card {
  background: #161618;
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 16px;
}

.card-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.instructor-name {
  font-size: 20px;
  font-weight: 600;
  color: #F5F5F7;
  margin: 0 0 4px;
}

.instructor-email {
  font-size: 13px;
  color: rgba(255,255,255,0.4);
  margin: 0;
}

.status-active {
  color: #30D158;
  background: rgba(48,209,88,0.1);
  padding: 3px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
}

.status-deactivated {
  color: rgba(255,255,255,0.3);
  background: rgba(255,255,255,0.06);
  padding: 3px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
}

.card-actions {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid rgba(255,255,255,0.06);
}

.section-heading {
  font-size: 13px;
  font-weight: 600;
  color: rgba(255,255,255,0.4);
  text-transform: uppercase;
  letter-spacing: 0.06em;
  margin: 0 0 20px;
}

.section-group {
  margin-bottom: 20px;
}

.section-group:last-child {
  margin-bottom: 0;
}

.section-name {
  font-size: 12px;
  font-weight: 600;
  color: rgba(255,255,255,0.35);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin: 0 0 8px;
}

.team-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.team-row {
  padding: 10px 14px;
  background: rgba(255,255,255,0.03);
  border-radius: 10px;
  border: 1px solid rgba(255,255,255,0.05);
}

.team-name {
  font-size: 14px;
  color: #F5F5F7;
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
  margin-top: 8px;
}

/* Dialog */
.confirm-card {
  background: #1C1C1E;
  border-radius: 16px;
  padding: 28px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.confirm-title {
  font-size: 18px;
  font-weight: 600;
  color: #F5F5F7;
  margin: 0;
}

.confirm-body {
  font-size: 14px;
  color: rgba(255,255,255,0.6);
  margin: 0;
}

.consequence-box {
  background: rgba(255,69,58,0.06);
  border: 1px solid rgba(255,69,58,0.2);
  border-radius: 10px;
  padding: 14px 16px;
}

.consequence-label {
  font-size: 12px;
  font-weight: 600;
  color: #FF453A;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin: 0 0 8px;
}

.consequence-list {
  margin: 0;
  padding-left: 18px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.consequence-list li {
  font-size: 13px;
  color: rgba(255,255,255,0.6);
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-label {
  font-size: 12px;
  font-weight: 500;
  color: rgba(255,255,255,0.45);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.reason-input {
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 14px;
  color: #F5F5F7;
  outline: none;
  resize: vertical;
  font-family: inherit;
  transition: border-color 0.15s;
}

.reason-input:focus {
  border-color: rgba(255,69,58,0.4);
}

.confirm-warning {
  font-size: 13px;
  color: rgba(255,255,255,0.5);
  margin: 0;
}

.confirm-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 4px;
}
</style>
