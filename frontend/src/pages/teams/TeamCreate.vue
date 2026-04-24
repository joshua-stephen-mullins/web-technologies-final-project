<template>
  <v-container class="pa-6" max-width="640">
    <div class="d-flex align-center gap-3 mb-6">
      <v-btn icon="mdi-arrow-left" variant="text" to="/teams" />
      <h1 class="text-h4">New Team</h1>
    </div>

    <v-alert v-if="error" type="error" class="mb-4">{{ error }}</v-alert>

    <v-card>
      <v-card-text class="pa-6">
        <v-form @submit.prevent="submit">
          <v-text-field v-model="form.name" label="Team Name" variant="outlined" class="mb-4"
                        :rules="[v => !!v || 'Team name is required']" />
          <v-textarea v-model="form.description" label="Description (optional)" variant="outlined" class="mb-4" rows="3" />
          <v-text-field v-model="form.websiteUrl" label="Website URL (optional)" variant="outlined" class="mb-4" />
          <v-select
            v-model="form.sectionId"
            :items="sections"
            item-title="name"
            item-value="id"
            label="Section"
            variant="outlined"
            class="mb-6"
            :rules="[v => !!v || 'Section is required']"
          />
          <v-btn type="submit" color="primary" :loading="loading" :disabled="!form.name || !form.sectionId">
            Create Team
          </v-btn>
        </v-form>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useTeamStore } from '@/stores/teamStore'
import { sectionApi } from '@/apis/sectionApi'

const router = useRouter()
const teamStore = useTeamStore()

const form = ref({ name: '', description: '', websiteUrl: '', sectionId: undefined as number | undefined })
const sections = ref<any[]>([])
const loading = ref(false)
const error = ref('')

onMounted(async () => {
  const res = await sectionApi.search()
  sections.value = res.data.data
})

async function submit() {
  loading.value = true
  error.value = ''
  try {
    const created = await teamStore.createTeam({
      name: form.value.name,
      description: form.value.description || undefined,
      websiteUrl: form.value.websiteUrl || undefined,
      sectionId: form.value.sectionId!,
    })
    router.push(`/teams/${created.id}`)
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? 'Failed to create team'
  } finally {
    loading.value = false
  }
}
</script>
