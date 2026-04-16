<template>
  <v-container class="pa-6" max-width="720">
    <div class="d-flex align-center gap-3 mb-6">
      <v-btn icon="mdi-arrow-left" variant="text" to="/rubrics" />
      <h1 class="text-h4">{{ editing ? 'Edit Rubric' : rubric?.name }}</h1>
      <v-spacer />
      <v-btn v-if="!editing" prepend-icon="mdi-pencil" variant="tonal" @click="startEdit">Edit</v-btn>
    </div>

    <v-alert v-if="error" type="error" class="mb-4">{{ error }}</v-alert>
    <v-alert v-if="success" type="success" class="mb-4">Rubric updated.</v-alert>

    <v-progress-circular v-if="loading" indeterminate class="d-block mx-auto my-8" />

    <v-card v-else-if="rubric">
      <v-card-text class="pa-6">
        <template v-if="!editing">
          <v-list>
            <v-list-item
              v-for="c in rubric.criteria"
              :key="c.id"
              :title="c.name"
              :subtitle="`${c.description} — Max: ${c.maxScore}`"
              prepend-icon="mdi-checkbox-marked-circle-outline"
            />
          </v-list>
          <div v-if="!rubric.criteria?.length" class="text-medium-emphasis">No criteria defined.</div>
        </template>

        <template v-else>
          <v-form @submit.prevent="save">
            <v-text-field v-model="editName" label="Rubric Name" variant="outlined" class="mb-6" />
            <div class="text-subtitle-1 mb-3">Criteria</div>
            <CriterionEditor v-model="editCriteria" />
            <div class="d-flex gap-3 mt-6">
              <v-btn type="submit" color="primary" :loading="saving">Save</v-btn>
              <v-btn variant="text" @click="editing = false">Cancel</v-btn>
            </div>
          </v-form>
        </template>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useRubricStore } from '@/stores/rubricStore'
import CriterionEditor, { type CriterionRow } from '@/components/rubric/CriterionEditor.vue'

const route = useRoute()
const rubricStore = useRubricStore()
const id = Number(route.params.id)

const rubric = ref<any>(null)
const loading = ref(false)
const error = ref('')
const success = ref(false)

const editing = ref(false)
const editName = ref('')
const editCriteria = ref<CriterionRow[]>([])
const saving = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    await rubricStore.fetchRubricById(id)
    rubric.value = rubricStore.currentRubric
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? 'Failed to load rubric'
  } finally {
    loading.value = false
  }
})

function startEdit() {
  editName.value = rubric.value.name
  editCriteria.value = rubric.value.criteria.map((c: any) => ({
    name: c.name,
    description: c.description,
    maxScore: c.maxScore,
  }))
  editing.value = true
}

async function save() {
  saving.value = true
  error.value = ''
  try {
    await rubricStore.updateRubric(id, { name: editName.value, criteria: editCriteria.value })
    rubric.value = rubricStore.currentRubric
    editing.value = false
    success.value = true
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? 'Update failed'
  } finally {
    saving.value = false
  }
}
</script>
