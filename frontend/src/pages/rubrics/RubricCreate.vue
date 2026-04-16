<template>
  <v-container class="pa-6" max-width="720">
    <div class="d-flex align-center gap-3 mb-6">
      <v-btn icon="mdi-arrow-left" variant="text" to="/rubrics" />
      <h1 class="text-h4">New Rubric</h1>
    </div>

    <v-alert v-if="error" type="error" class="mb-4">{{ error }}</v-alert>

    <v-card>
      <v-card-text class="pa-6">
        <v-form @submit.prevent="submit">
          <v-text-field
            v-model="name"
            label="Rubric Name"
            variant="outlined"
            class="mb-6"
            :rules="[v => !!v || 'Name is required']"
          />

          <div class="text-subtitle-1 mb-3">Criteria</div>
          <CriterionEditor v-model="criteria" />

          <v-btn type="submit" color="primary" class="mt-6" :loading="loading" :disabled="!name">
            Create Rubric
          </v-btn>
        </v-form>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useRubricStore } from '@/stores/rubricStore'
import CriterionEditor, { type CriterionRow } from '@/components/rubric/CriterionEditor.vue'

const router = useRouter()
const rubricStore = useRubricStore()

const name = ref('')
const criteria = ref<CriterionRow[]>([])
const loading = ref(false)
const error = ref('')

async function submit() {
  loading.value = true
  error.value = ''
  try {
    await rubricStore.createRubric({ name: name.value, criteria: criteria.value })
    router.push('/rubrics')
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? 'Failed to create rubric'
  } finally {
    loading.value = false
  }
}
</script>
