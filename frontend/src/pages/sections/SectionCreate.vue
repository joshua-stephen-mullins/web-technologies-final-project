<template>
  <v-container class="pa-6" max-width="640">
    <div class="d-flex align-center gap-3 mb-6">
      <v-btn icon="mdi-arrow-left" variant="text" to="/sections" />
      <h1 class="text-h4">New Section</h1>
    </div>

    <v-alert v-if="error" type="error" class="mb-4">{{ error }}</v-alert>

    <v-card>
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
          <v-btn type="submit" color="primary" :loading="loading" :disabled="!form.name || !form.startDate || !form.endDate">
            Create Section
          </v-btn>
        </v-form>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useSectionStore } from '@/stores/sectionStore'
import { rubricApi } from '@/apis/rubricApi'

const router = useRouter()
const sectionStore = useSectionStore()

const form = ref({ name: '', startDate: '', endDate: '', rubricId: undefined as number | undefined })
const rubrics = ref<any[]>([])
const loading = ref(false)
const error = ref('')

onMounted(async () => {
  const res = await rubricApi.getAll()
  rubrics.value = res.data.data
})

async function submit() {
  loading.value = true
  error.value = ''
  try {
    await sectionStore.createSection(form.value)
    router.push('/sections')
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? 'Failed to create section'
  } finally {
    loading.value = false
  }
}
</script>
