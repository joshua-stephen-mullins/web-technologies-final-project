<template>
  <div class="assign-page">
    <div class="page-header">
      <v-btn icon="mdi-arrow-left" variant="text" to="/instructors" />
      <div>
        <h1 class="page-title">Assign Instructors to Teams</h1>
        <p class="page-sub">Select a team, then choose which instructors to assign</p>
      </div>
    </div>

    <div v-if="loadError" class="error-msg">{{ loadError }}</div>

    <div v-if="!loadError" class="assign-layout">
      <!-- Teams Panel -->
      <div class="panel">
        <h2 class="panel-title">Teams</h2>
        <div v-if="store.loading" class="empty-msg">Loading...</div>
        <template v-else>
          <div
            v-for="team in store.teams"
            :key="team.id"
            class="list-item"
            :class="{ selected: selectedTeam?.id === team.id }"
            @click="selectTeam(team)"
          >
            <span class="item-name">{{ team.name }}</span>
            <span class="badge">{{ (teamInstructorMap[team.id] ?? []).length }} assigned</span>
          </div>
          <div v-if="store.teams.length === 0" class="empty-msg">No teams found</div>
        </template>
      </div>

      <!-- Instructors Panel -->
      <div class="panel">
        <h2 class="panel-title">
          Instructors
          <span v-if="selectedTeam" class="panel-sub">— {{ selectedTeam.name }}</span>
        </h2>
        <div v-if="!selectedTeam" class="empty-msg">Select a team to manage its instructors</div>
        <template v-else>
          <div v-for="instructor in store.instructors" :key="instructor.id" class="list-item instructor-item">
            <label class="instructor-row">
              <input
                type="checkbox"
                :checked="selectedIds.has(instructor.id)"
                @change="toggleSelection(instructor.id)"
                class="instructor-checkbox"
              />
              <span class="instructor-name">{{ instructor.firstName }} {{ instructor.lastName }}</span>
              <span class="instructor-email">{{ instructor.email }}</span>
              <span v-if="isCurrentlyAssigned(instructor.id)" class="assigned-badge">Assigned</span>
            </label>
            <v-btn
              v-if="isCurrentlyAssigned(instructor.id)"
              icon="mdi-close"
              size="x-small"
              variant="text"
              color="error"
              @click.stop="promptRemove(instructor)"
            />
          </div>
          <div v-if="store.instructors.length === 0" class="empty-msg">No instructors found</div>
        </template>
      </div>
    </div>

    <!-- Action Bar -->
    <div v-if="selectedTeam && pendingIds.length > 0" class="action-bar">
      <span class="pending-label">{{ pendingIds.length }} new instructor(s) to assign</span>
      <v-btn color="primary" rounded="xl" @click="openConfirm">Review & Confirm</v-btn>
    </div>

    <!-- Confirmation Dialog -->
    <v-dialog v-model="confirmDialog" max-width="480">
      <div class="confirm-card">
        <h2 class="confirm-title">Confirm Assignment</h2>
        <p class="confirm-body">
          Assign the following to <strong>{{ selectedTeam?.name }}</strong>?
        </p>
        <ul class="confirm-list">
          <li v-for="id in pendingIds" :key="id">{{ getInstructorLabel(id) }}</li>
        </ul>
        <p class="confirm-note">Each instructor will receive an email notification.</p>
        <div v-if="submitError" class="error-msg">{{ submitError }}</div>
        <div class="confirm-actions">
          <v-btn variant="text" @click="confirmDialog = false">Cancel</v-btn>
          <v-btn color="primary" rounded="xl" :loading="submitting" @click="submitAssignment">
            Confirm
          </v-btn>
        </div>
      </div>
    </v-dialog>

    <!-- Remove Confirmation Dialog -->
    <v-dialog v-model="removeDialog" max-width="440">
      <div class="confirm-card">
        <h2 class="confirm-title">Remove Instructor</h2>
        <p class="confirm-body">
          Remove <strong>{{ instructorToRemove?.firstName }} {{ instructorToRemove?.lastName }}</strong>
          from <strong>{{ selectedTeam?.name }}</strong>?
        </p>
        <p class="confirm-note">They will receive an email notification about this removal.</p>
        <div v-if="removeError" class="error-msg">{{ removeError }}</div>
        <div class="confirm-actions">
          <v-btn variant="text" @click="removeDialog = false">Cancel</v-btn>
          <v-btn color="error" rounded="xl" :loading="removing" @click="confirmRemove">
            Remove
          </v-btn>
        </div>
      </div>
    </v-dialog>

    <div v-if="successMsg" class="success-msg">{{ successMsg }}</div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from 'vue'
import { useInstructorStore } from '@/stores/instructorStore'

const store = useInstructorStore()

const selectedTeam = ref<any>(null)
const teamInstructorMap = reactive<Record<number, any[]>>({})
const selectedIds = ref(new Set<number>())
const confirmDialog = ref(false)
const submitting = ref(false)
const submitError = ref('')
const removeDialog = ref(false)
const instructorToRemove = ref<any>(null)
const removing = ref(false)
const removeError = ref('')
const successMsg = ref('')
const loadError = ref('')

onMounted(async () => {
  try {
    await Promise.all([store.fetchTeams(), store.fetchInstructors()])
  } catch {
    loadError.value = 'Failed to load data. Please refresh and try again.'
  }
})

async function selectTeam(team: any) {
  selectedTeam.value = team
  successMsg.value = ''
  submitError.value = ''

  if (!(team.id in teamInstructorMap)) {
    try {
      teamInstructorMap[team.id] = await store.fetchTeamInstructors(team.id)
    } catch {
      teamInstructorMap[team.id] = []
    }
  }

  selectedIds.value = new Set((teamInstructorMap[team.id] ?? []).map((i: any) => i.id))
}

function isCurrentlyAssigned(instructorId: number): boolean {
  if (!selectedTeam.value) return false
  return (teamInstructorMap[selectedTeam.value.id] ?? []).some((i: any) => i.id === instructorId)
}

function toggleSelection(instructorId: number) {
  const next = new Set(selectedIds.value)
  if (next.has(instructorId)) {
    next.delete(instructorId)
  } else {
    next.add(instructorId)
  }
  selectedIds.value = next
}

const pendingIds = computed<number[]>(() => {
  if (!selectedTeam.value) return []
  const assigned = new Set((teamInstructorMap[selectedTeam.value.id] ?? []).map((i: any) => i.id))
  return [...selectedIds.value].filter(id => !assigned.has(id))
})

function getInstructorLabel(id: number): string {
  const inst = store.instructors.find((i: any) => i.id === id)
  return inst ? `${inst.firstName} ${inst.lastName} (${inst.email})` : `Instructor #${id}`
}

function openConfirm() {
  submitError.value = ''
  confirmDialog.value = true
}

function promptRemove(instructor: any) {
  instructorToRemove.value = instructor
  removeError.value = ''
  successMsg.value = ''
  removeDialog.value = true
}

async function confirmRemove() {
  removing.value = true
  removeError.value = ''
  try {
    await store.removeInstructorFromTeam(selectedTeam.value.id, instructorToRemove.value.id)
    const updated = teamInstructorMap[selectedTeam.value.id].filter(
      (i: any) => i.id !== instructorToRemove.value.id
    )
    teamInstructorMap[selectedTeam.value.id] = updated
    selectedIds.value = new Set(updated.map((i: any) => i.id))
    removeDialog.value = false
    successMsg.value = `${instructorToRemove.value.firstName} ${instructorToRemove.value.lastName} removed from "${selectedTeam.value.name}". Notification sent.`
  } catch (e: any) {
    removeError.value = e?.response?.data?.message ?? 'Removal failed. Please try again.'
  } finally {
    removing.value = false
  }
}

async function submitAssignment() {
  submitting.value = true
  submitError.value = ''
  const ids = [...pendingIds.value]
  const count = ids.length
  try {
    const updated = await store.assignInstructorsToTeam(selectedTeam.value.id, ids)
    teamInstructorMap[selectedTeam.value.id] = updated
    selectedIds.value = new Set(updated.map((i: any) => i.id))
    confirmDialog.value = false
    successMsg.value = `${count} instructor(s) assigned to "${selectedTeam.value.name}". Notifications sent.`
  } catch (e: any) {
    submitError.value = e?.response?.data?.message ?? 'Assignment failed. Please try again.'
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.assign-page {
  padding: 40px 32px 80px;
  max-width: 960px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 32px;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: #F5F5F7;
  margin: 0;
}

.page-sub {
  font-size: 13px;
  color: rgba(255,255,255,0.4);
  margin: 4px 0 0;
}

.assign-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.panel {
  background: #161618;
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 16px;
  padding: 20px;
  min-height: 300px;
}

.panel-title {
  font-size: 14px;
  font-weight: 600;
  color: rgba(255,255,255,0.6);
  text-transform: uppercase;
  letter-spacing: 0.06em;
  margin: 0 0 16px;
}

.panel-sub {
  font-weight: 400;
  text-transform: none;
  letter-spacing: 0;
  color: #F5F5F7;
  font-size: 14px;
}

.list-item {
  padding: 10px 12px;
  border-radius: 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 4px;
  transition: background 0.15s;
}

.list-item:hover {
  background: rgba(255,255,255,0.05);
}

.list-item.selected {
  background: rgba(10,132,255,0.12);
  border: 1px solid rgba(10,132,255,0.3);
}

.list-item.instructor-item {
  cursor: default;
  padding: 8px 12px;
}

.item-name {
  font-size: 14px;
  color: #F5F5F7;
}

.badge {
  font-size: 11px;
  color: rgba(255,255,255,0.4);
  background: rgba(255,255,255,0.06);
  padding: 2px 8px;
  border-radius: 20px;
  white-space: nowrap;
}

.instructor-row {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  cursor: pointer;
}

.instructor-checkbox {
  accent-color: #0A84FF;
  width: 15px;
  height: 15px;
  flex-shrink: 0;
}

.instructor-name {
  font-size: 14px;
  color: #F5F5F7;
  flex: 1;
}

.instructor-email {
  font-size: 12px;
  color: rgba(255,255,255,0.35);
}

.assigned-badge {
  font-size: 11px;
  color: #30D158;
  background: rgba(48,209,88,0.1);
  padding: 2px 8px;
  border-radius: 20px;
  white-space: nowrap;
}

.empty-msg {
  font-size: 13px;
  color: rgba(255,255,255,0.25);
  padding: 20px 0;
  text-align: center;
}

.action-bar {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 24px;
  padding: 16px 20px;
  background: #161618;
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 12px;
}

.pending-label {
  font-size: 13px;
  color: rgba(255,255,255,0.5);
}

.confirm-card {
  background: #1C1C1E;
  border-radius: 16px;
  padding: 28px;
  display: flex;
  flex-direction: column;
  gap: 12px;
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

.confirm-list {
  margin: 0;
  padding-left: 20px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.confirm-list li {
  font-size: 14px;
  color: #F5F5F7;
}

.confirm-note {
  font-size: 12px;
  color: rgba(255,255,255,0.3);
  margin: 0;
}

.confirm-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 4px;
}

.error-msg {
  font-size: 13px;
  color: #FF453A;
  padding: 10px 14px;
  background: rgba(255,69,58,0.08);
  border-radius: 8px;
  margin-bottom: 16px;
}

.success-msg {
  font-size: 13px;
  color: #30D158;
  padding: 10px 14px;
  background: rgba(48,209,88,0.08);
  border-radius: 8px;
  margin-top: 16px;
}
</style>
