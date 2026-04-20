<template>
  <v-container class="pa-6" max-width="640">
    <div class="d-flex align-center gap-3 mb-6">
      <v-btn icon="mdi-arrow-left" variant="text" :to="`/teams/${route.params.id}`" />
      <h1 class="text-h4">Edit Team</h1>
    </div>

    <v-alert v-if="error" type="error" class="mb-4">{{ error }}</v-alert>

    <div v-if="loadingTeam" class="d-flex justify-center pa-8">
      <v-progress-circular indeterminate color="primary" />
    </div>

    <v-card v-else>
      <v-card-text class="pa-6">
        <v-form @submit.prevent="submit">
          <v-text-field v-model="form.name" label="Team Name" variant="outlined" class="mb-4" />
          <v-textarea v-model="form.description" label="Description" variant="outlined" class="mb-4" rows="3" />
          <v-text-field v-model="form.websiteUrl" label="Website URL" variant="outlined" class="mb-6" />
          <div class="d-flex gap-3">
            <v-btn type="submit" color="primary" :loading="saving" :disabled="!form.name">Save Changes</v-btn>
            <v-btn variant="text" :to="`/teams/${route.params.id}`">Cancel</v-btn>
          </div>
        </v-form>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTeamStore } from '@/stores/teamStore'

const route = useRoute()
const router = useRouter()
const teamStore = useTeamStore()

const form = ref({ name: '', description: '', websiteUrl: '' })
const loadingTeam = ref(true)
const saving = ref(false)
const error = ref('')

onMounted(async () => {
  try {
    await teamStore.fetchTeamById(Number(route.params.id))
    const t = teamStore.currentTeam
    if (t) {
      form.value = { name: t.name, description: t.description ?? '', websiteUrl: t.websiteUrl ?? '' }
    }
  } finally {
    loadingTeam.value = false
  }
})

async function submit() {
  saving.value = true
  error.value = ''
  try {
    await teamStore.updateTeam(Number(route.params.id), {
      name: form.value.name,
      description: form.value.description || undefined,
      websiteUrl: form.value.websiteUrl || undefined,
    })
    router.push(`/teams/${route.params.id}`)
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? 'Failed to update team'
  } finally {
    saving.value = false
  }
}
</script>
