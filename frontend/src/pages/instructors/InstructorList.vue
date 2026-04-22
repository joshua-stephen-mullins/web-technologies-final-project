<template>
  <div class="list-page">
    <div class="page-header">
      <div>
        <h1 class="page-title">Instructors</h1>
        <p class="page-sub">Search and manage instructor accounts</p>
      </div>
      <v-btn color="primary" rounded="xl" to="/instructors/invite">Invite Instructors</v-btn>
    </div>

    <!-- Search Form -->
    <div class="search-card">
      <div class="search-grid">
        <div class="field-group">
          <label class="field-label">First Name</label>
          <input v-model="form.firstName" class="field-input" placeholder="Any" @keyup.enter="search" />
        </div>
        <div class="field-group">
          <label class="field-label">Last Name</label>
          <input v-model="form.lastName" class="field-input" placeholder="Any" @keyup.enter="search" />
        </div>
        <div class="field-group">
          <label class="field-label">Team Name</label>
          <input v-model="form.teamName" class="field-input" placeholder="Any" @keyup.enter="search" />
        </div>
        <div class="field-group">
          <label class="field-label">Status</label>
          <select v-model="form.status" class="field-input field-select">
            <option value="">Any</option>
            <option value="active">Active</option>
            <option value="deactivated">Deactivated</option>
          </select>
        </div>
      </div>
      <div class="search-actions">
        <v-btn variant="text" size="small" @click="clearForm">Clear</v-btn>
        <v-btn color="primary" rounded="xl" :loading="store.loading" @click="search">Search</v-btn>
      </div>
    </div>

    <!-- Error -->
    <div v-if="errorMsg" class="error-msg">{{ errorMsg }}</div>

    <!-- Results -->
    <div v-if="searched" class="results-section">
      <p class="results-label">
        {{ store.instructors.length === 0 ? 'No matching instructors found.' : `${store.instructors.length} instructor(s) found` }}
      </p>

      <div v-if="store.instructors.length > 0" class="results-table">
        <div class="table-header">
          <span>Name</span>
          <span>Teams</span>
          <span>Status</span>
        </div>
        <div
          v-for="instructor in store.instructors"
          :key="instructor.id"
          class="table-row"
          @click="goToDetail(instructor.id)"
        >
          <span class="cell-name">{{ instructor.firstName }} {{ instructor.lastName }}</span>
          <span class="cell-teams">
            <span v-if="!instructor.teamNames || instructor.teamNames.length === 0" class="no-teams">—</span>
            <span v-else class="team-chips">
              <span v-for="name in instructor.teamNames" :key="name" class="team-chip">{{ name }}</span>
            </span>
          </span>
          <span class="cell-status">
            <span :class="instructor.enabled ? 'status-active' : 'status-deactivated'">
              {{ instructor.enabled ? 'Active' : 'Deactivated' }}
            </span>
          </span>
        </div>
      </div>

      <!-- 4a2: extension actions when no results -->
      <div v-if="store.instructors.length === 0" class="empty-actions">
        <v-btn color="primary" rounded="xl" to="/instructors/invite">Invite Instructors</v-btn>
        <v-btn variant="text" @click="clearForm">Modify Search</v-btn>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useInstructorStore } from '@/stores/instructorStore'

// Owner: Whitney (Person 3) | Admin only
// UC-21: Find instructors by firstName, lastName, teamName, status

const store = useInstructorStore()
const router = useRouter()

const searched = ref(false)
const errorMsg = ref('')

const form = reactive({
  firstName: '',
  lastName: '',
  teamName: '',
  status: '',
})

async function search() {
  errorMsg.value = ''
  const params: Record<string, string> = {}
  if (form.firstName.trim()) params.firstName = form.firstName.trim()
  if (form.lastName.trim()) params.lastName = form.lastName.trim()
  if (form.teamName.trim()) params.teamName = form.teamName.trim()
  if (form.status) params.status = form.status

  try {
    await store.fetchInstructors(params)
    searched.value = true
  } catch {
    errorMsg.value = 'Search failed. Please try again.'
  }
}

function clearForm() {
  form.firstName = ''
  form.lastName = ''
  form.teamName = ''
  form.status = ''
  searched.value = false
  store.instructors = []
  errorMsg.value = ''
}

function goToDetail(id: number) {
  router.push(`/instructors/${id}`)
}
</script>

<style scoped>
.list-page {
  padding: 40px 32px 80px;
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 28px;
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

.search-card {
  background: #161618;
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
}

.search-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 20px;
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

.field-input {
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 8px;
  padding: 8px 12px;
  font-size: 14px;
  color: #F5F5F7;
  outline: none;
  transition: border-color 0.15s;
}

.field-input:focus {
  border-color: rgba(10,132,255,0.5);
}

.field-select {
  cursor: pointer;
  appearance: none;
}

.field-select option {
  background: #1C1C1E;
  color: #F5F5F7;
}

.search-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 8px;
}

.results-section {
  margin-top: 8px;
}

.results-label {
  font-size: 13px;
  color: rgba(255,255,255,0.4);
  margin: 0 0 12px;
}

.results-table {
  background: #161618;
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 16px;
  overflow: hidden;
}

.table-header {
  display: grid;
  grid-template-columns: 2fr 3fr 1fr;
  gap: 16px;
  padding: 12px 20px;
  font-size: 11px;
  font-weight: 600;
  color: rgba(255,255,255,0.35);
  text-transform: uppercase;
  letter-spacing: 0.06em;
  border-bottom: 1px solid rgba(255,255,255,0.06);
}

.table-row {
  display: grid;
  grid-template-columns: 2fr 3fr 1fr;
  gap: 16px;
  padding: 14px 20px;
  border-bottom: 1px solid rgba(255,255,255,0.04);
  cursor: pointer;
  transition: background 0.12s;
  align-items: center;
}

.table-row:last-child {
  border-bottom: none;
}

.table-row:hover {
  background: rgba(255,255,255,0.03);
}

.cell-name {
  font-size: 14px;
  color: #F5F5F7;
  font-weight: 500;
}

.cell-teams {
  font-size: 13px;
}

.no-teams {
  color: rgba(255,255,255,0.2);
}

.team-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.team-chip {
  font-size: 11px;
  color: rgba(255,255,255,0.6);
  background: rgba(255,255,255,0.07);
  padding: 2px 8px;
  border-radius: 20px;
}

.cell-status {
  font-size: 13px;
}

.status-active {
  color: #30D158;
  background: rgba(48,209,88,0.1);
  padding: 2px 10px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 500;
}

.status-deactivated {
  color: rgba(255,255,255,0.3);
  background: rgba(255,255,255,0.06);
  padding: 2px 10px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 500;
}

.empty-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.error-msg {
  font-size: 13px;
  color: #FF453A;
  padding: 10px 14px;
  background: rgba(255,69,58,0.08);
  border-radius: 8px;
  margin-bottom: 16px;
}
</style>
