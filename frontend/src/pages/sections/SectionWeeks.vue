<template>
  <v-container class="pa-6" max-width="720">
    <div class="d-flex align-center gap-3 mb-6">
      <v-btn icon="mdi-arrow-left" variant="text" :to="`/sections/${id}`" />
      <h1 class="text-h4">Manage Weeks</h1>
    </div>

    <v-alert v-if="saved" type="success" class="mb-4" closable @click:close="saved = false">
      Active weeks updated.
    </v-alert>
    <v-alert v-if="error" type="error" class="mb-4">{{ error }}</v-alert>

    <v-progress-circular v-if="loading" indeterminate class="d-block mx-auto my-8" />

    <v-card v-else>
      <v-card-text class="pa-4">
        <p class="text-body-2 text-medium-emphasis mb-4">
          Toggle weeks inactive to exclude them from WAR and peer evaluation windows.
          (BR-2: Fall active weeks 5–15; Spring active weeks 1–15)
        </p>
        <v-table>
          <thead>
            <tr>
              <th>Week #</th>
              <th>Start Date</th>
              <th>End Date</th>
              <th>Active</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="week in weeks" :key="week.id">
              <td>{{ week.weekNumber }}</td>
              <td>{{ week.startDate }}</td>
              <td>{{ week.endDate }}</td>
              <td>
                <v-switch
                  v-model="week.isActive"
                  color="success"
                  hide-details
                  density="compact"
                />
              </td>
            </tr>
          </tbody>
        </v-table>
      </v-card-text>
      <v-card-actions class="pa-4">
        <v-btn color="primary" :loading="saving" @click="save">Save Active Weeks</v-btn>
      </v-card-actions>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useSectionStore } from '@/stores/sectionStore'

const route = useRoute()
const sectionStore = useSectionStore()
const id = Number(route.params.id)

const weeks = ref<any[]>([])
const loading = ref(false)
const saving = ref(false)
const saved = ref(false)
const error = ref('')

onMounted(async () => {
  loading.value = true
  try {
    await sectionStore.fetchWeeks(id)
    // Deep copy so we can mutate locally
    weeks.value = sectionStore.weeks.map((w) => ({ ...w }))
  } finally {
    loading.value = false
  }
})

async function save() {
  saving.value = true
  error.value = ''
  try {
    const inactiveWeekNumbers = weeks.value.filter((w) => !w.isActive).map((w) => w.weekNumber)
    await sectionStore.updateActiveWeeks(id, inactiveWeekNumbers)
    saved.value = true
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? 'Failed to save'
  } finally {
    saving.value = false
  }
}
</script>
