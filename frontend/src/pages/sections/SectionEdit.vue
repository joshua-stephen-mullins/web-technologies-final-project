<template>
  <v-container class="pa-6" max-width="640">
    <div class="d-flex align-center gap-3 mb-6">
      <v-btn icon="mdi-arrow-left" variant="text" :to="`/sections/${id}`" />
      <h1 class="text-h4">Edit Section</h1>
    </div>

    <v-alert v-if="error" type="error" class="mb-4">{{ error }}</v-alert>
    <v-progress-circular v-if="loading" indeterminate class="d-block mx-auto my-8" />

    <v-card v-else>
      <v-card-text class="pa-6">
        <v-form @submit.prevent="submit">
          <v-text-field v-model="form.name" label="Section Name" variant="outlined" class="mb-4" />
          <v-text-field v-model="form.startDate" label="Start Date" type="date" variant="outlined" class="mb-4" />
          <v-text-field v-model="form.endDate" label="End Date" type="date" variant="outlined" class="mb-4" />
          <v-select
            v-model="form.rubricId"
            :items="rubrics"
            item-title="name"
            item-value="id"
            label="Rubric (optional)"
            variant="outlined"
            clearable
            class="mb-6"
          />
          <div class="d-flex gap-3">
            <v-btn type="submit" color="primary" :loading="saving">Save Changes</v-btn>
            <v-btn variant="text" :to="`/sections/${id}`">Cancel</v-btn>
          </div>
        </v-form>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useSectionStore } from '@/stores/sectionStore'
import { rubricApi } from '@/apis/rubricApi'

const route = useRoute()
const router = useRouter()
const sectionStore = useSectionStore()
const id = Number(route.params.id)

const form = ref({ name: '', startDate: '', endDate: '', rubricId: undefined as number | undefined })
const rubrics = ref<any[]>([])
const loading = ref(false)
const saving = ref(false)
const error = ref('')

onMounted(async () => {
  loading.value = true
  try {
    await sectionStore.fetchSectionById(id)
    const s = sectionStore.currentSection
    form.value = { name: s.name, startDate: s.startDate, endDate: s.endDate, rubricId: s.rubricId }
    const res = await rubricApi.getAll()
    rubrics.value = res.data.data
  } finally {
    loading.value = false
  }
})

async function submit() {
  saving.value = true
  error.value = ''
  try {
    await sectionStore.updateSection(id, form.value)
    router.push(`/sections/${id}`)
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? 'Update failed'
  } finally {
    saving.value = false
  }
}
</script>
