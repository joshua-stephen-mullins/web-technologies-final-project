<template>
  <v-container class="pa-6" max-width="960">
    <div class="d-flex align-center gap-3 mb-6">
      <div class="flex-1">
        <h1 class="text-h5 font-weight-bold">Weekly Activity Report</h1>
        <p class="text-caption text-medium-emphasis">Log your activities for each week</p>
      </div>
      <v-btn variant="tonal" size="small" to="/activities/report">View Team Report</v-btn>
    </div>

    <v-alert v-if="error" type="error" class="mb-4">{{ error }}</v-alert>

    <!-- Week selector -->
    <v-card class="mb-4">
      <v-card-text class="pa-4">
        <v-select
          v-model="selectedWeekId"
          :items="availableWeeks"
          item-title="label"
          item-value="id"
          label="Select Week"
          variant="outlined"
          density="compact"
          :loading="loadingWeeks"
          @update:model-value="loadActivities"
        />
      </v-card-text>
    </v-card>

    <template v-if="selectedWeekId">
      <!-- Activities table -->
      <v-card class="mb-4">
        <v-card-title class="d-flex align-center justify-space-between pa-4 pb-2">
          <span>Activities ({{ activityStore.activities.length }})</span>
          <v-btn color="primary" size="small" prepend-icon="mdi-plus" @click="openAdd">Add Activity</v-btn>
        </v-card-title>
        <v-card-text class="pa-0">
          <div v-if="activityStore.loading" class="d-flex justify-center pa-8">
            <v-progress-circular indeterminate color="primary" size="24" />
          </div>
          <div v-else-if="!activityStore.activities.length" class="pa-6 text-center text-caption text-medium-emphasis">
            No activities logged for this week yet.
          </div>
          <div v-else class="table-wrap">
            <table class="activity-table">
              <thead>
                <tr>
                  <th>Category</th>
                  <th>Description</th>
                  <th class="text-right">Planned hrs</th>
                  <th class="text-right">Actual hrs</th>
                  <th>Status</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                <ActivityRow
                  v-for="act in activityStore.activities"
                  :key="act.id"
                  :activity="act"
                  @edit="openEdit"
                  @delete="deleteActivity"
                />
              </tbody>
            </table>
          </div>
        </v-card-text>
      </v-card>
    </template>

    <!-- Add/Edit dialog -->
    <v-dialog v-model="showDialog" max-width="560" persistent>
      <v-card>
        <v-card-title class="pa-4">{{ editing ? 'Edit Activity' : 'Add Activity' }}</v-card-title>
        <v-card-text class="pa-4 pt-0">
          <v-select
            v-model="form.category"
            :items="categories"
            label="Category"
            variant="outlined"
            density="compact"
            class="mb-3"
          />
          <v-textarea
            v-model="form.description"
            label="Description"
            variant="outlined"
            density="compact"
            rows="3"
            class="mb-3"
          />
          <div class="d-flex gap-3 mb-3">
            <v-text-field
              v-model.number="form.plannedHours"
              label="Planned Hours"
              type="number"
              min="0"
              step="0.5"
              variant="outlined"
              density="compact"
            />
            <v-text-field
              v-model.number="form.actualHours"
              label="Actual Hours"
              type="number"
              min="0"
              step="0.5"
              variant="outlined"
              density="compact"
            />
          </div>
          <v-select
            v-model="form.status"
            :items="statuses"
            label="Status"
            variant="outlined"
            density="compact"
          />
        </v-card-text>
        <v-card-actions class="pa-4 pt-0">
          <v-spacer />
          <v-btn variant="text" @click="showDialog = false">Cancel</v-btn>
          <v-btn color="primary" :loading="saving" :disabled="!form.category || !form.description || !form.plannedHours || !form.status"
                 @click="saveActivity">
            {{ editing ? 'Save' : 'Add' }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useActivityStore } from '@/stores/activityStore'
import { activityApi } from '@/apis/activityApi'
import ActivityRow from '@/components/activity/ActivityRow.vue'

const activityStore = useActivityStore()

const selectedWeekId = ref<number | null>(null)
const availableWeeks = ref<any[]>([])
const loadingWeeks = ref(false)
const error = ref('')

const showDialog = ref(false)
const editing = ref<any>(null)
const saving = ref(false)

const form = ref({ category: '', description: '', plannedHours: 0, actualHours: undefined as number | undefined, status: '' })

const categories = [
  'DEVELOPMENT', 'TESTING', 'BUGFIX', 'COMMUNICATION', 'DOCUMENTATION',
  'DESIGN', 'PLANNING', 'LEARNING', 'DEPLOYMENT', 'SUPPORT', 'MISCELLANEOUS',
]
const statuses = [
  { title: 'In Progress', value: 'IN_PROGRESS' },
  { title: 'Under Testing', value: 'UNDER_TESTING' },
  { title: 'Done', value: 'DONE' },
]

onMounted(async () => {
  loadingWeeks.value = true
  try {
    const res = await activityApi.getMyWeeks()
    const today = new Date()
    availableWeeks.value = res.data.data
      .filter((w: any) => new Date(w.startDate) <= today)
      .map((w: any) => ({ id: w.id, label: `Week ${w.weekNumber}: ${w.startDate} – ${w.endDate}` }))
      .reverse()
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? 'Failed to load weeks.'
  } finally {
    loadingWeeks.value = false
  }
})

async function loadActivities() {
  if (!selectedWeekId.value) return
  error.value = ''
  try {
    await activityStore.fetchActivities(selectedWeekId.value)
  } catch {
    error.value = 'Failed to load activities.'
  }
}

function openAdd() {
  editing.value = null
  form.value = { category: '', description: '', plannedHours: 0, actualHours: undefined, status: '' }
  showDialog.value = true
}

function openEdit(activity: any) {
  editing.value = activity
  form.value = {
    category: activity.category,
    description: activity.description,
    plannedHours: activity.plannedHours,
    actualHours: activity.actualHours,
    status: activity.status,
  }
  showDialog.value = true
}

async function saveActivity() {
  saving.value = true
  error.value = ''
  try {
    const payload = {
      weekId: selectedWeekId.value!,
      category: form.value.category,
      description: form.value.description,
      plannedHours: form.value.plannedHours,
      actualHours: form.value.actualHours,
      status: form.value.status,
    }
    if (editing.value) {
      await activityStore.updateActivity(editing.value.id, payload)
    } else {
      await activityStore.createActivity(payload)
    }
    showDialog.value = false
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? 'Failed to save activity'
  } finally {
    saving.value = false
  }
}

async function deleteActivity(id: number) {
  try {
    await activityStore.deleteActivity(id)
  } catch {
    error.value = 'Failed to delete activity.'
  }
}
</script>

<style scoped>
.table-wrap { overflow-x: auto; }
.activity-table { width: 100%; border-collapse: collapse; }
.activity-table th {
  padding: 10px 12px;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: rgba(255,255,255,0.4);
  text-align: left;
  border-bottom: 1px solid rgba(255,255,255,0.08);
}
.activity-table th.text-right { text-align: right; }
</style>
