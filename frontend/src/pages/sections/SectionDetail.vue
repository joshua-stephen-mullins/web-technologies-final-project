<template>
  <v-container class="pa-6" max-width="720">
    <div class="d-flex align-center gap-3 mb-6">
      <v-btn icon="mdi-arrow-left" variant="text" to="/sections" />
      <h1 class="text-h4">{{ section?.name ?? 'Section' }}</h1>
      <v-spacer />
      <v-btn v-if="section" prepend-icon="mdi-pencil" variant="tonal" :to="`/sections/${section.id}/edit`">
        Edit
      </v-btn>
      <v-btn v-if="section" prepend-icon="mdi-calendar-week" variant="tonal" :to="`/sections/${section.id}/weeks`">
        Weeks
      </v-btn>
    </div>

    <v-progress-circular v-if="loading" indeterminate class="d-block mx-auto my-8" />

    <template v-else-if="section">
      <v-card class="mb-4">
        <v-card-text>
          <v-row>
            <v-col cols="6">
              <div class="text-caption text-medium-emphasis">Start Date</div>
              <div>{{ section.startDate }}</div>
            </v-col>
            <v-col cols="6">
              <div class="text-caption text-medium-emphasis">End Date</div>
              <div>{{ section.endDate }}</div>
            </v-col>
            <v-col cols="12">
              <div class="text-caption text-medium-emphasis">Rubric</div>
              <div>{{ section.rubricName ?? 'None assigned' }}</div>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>

      <v-card>
        <v-card-title class="pa-4">Weeks</v-card-title>
        <v-card-text>
          <v-chip
            v-for="week in weeks"
            :key="week.id"
            :color="week.isActive ? 'success' : 'default'"
            variant="tonal"
            class="ma-1"
          >
            Week {{ week.weekNumber }}
            <span class="ml-1 text-caption">({{ week.startDate }})</span>
          </v-chip>
          <div v-if="!weeks.length" class="text-medium-emphasis">No weeks generated.</div>
        </v-card-text>
      </v-card>
    </template>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useSectionStore } from '@/stores/sectionStore'

const route = useRoute()
const sectionStore = useSectionStore()
const id = Number(route.params.id)

const section = ref<any>(null)
const weeks = ref<any[]>([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    await sectionStore.fetchSectionById(id)
    section.value = sectionStore.currentSection
    await sectionStore.fetchWeeks(id)
    weeks.value = sectionStore.weeks
  } finally {
    loading.value = false
  }
})
</script>
