<template>
  <v-container class="pa-6" max-width="640">
    <div class="d-flex align-center gap-3 mb-6">
      <v-btn icon="mdi-arrow-left" variant="text" to="/students" />
      <h1 class="text-h4">Invite Students</h1>
    </div>

    <v-alert v-if="error" type="error" class="mb-4">{{ error }}</v-alert>
    <v-alert v-if="successMsg" type="success" class="mb-4">{{ successMsg }}</v-alert>

    <v-card>
      <v-card-text class="pa-6">
        <p class="text-body-2 text-medium-emphasis mb-4">
          Enter student emails separated by semicolons. Each student will receive a registration link.
        </p>
        <v-textarea
          v-model="emailInput"
          label="Student Emails"
          placeholder="alice@tcu.edu; bob@tcu.edu; carol@tcu.edu"
          variant="outlined"
          rows="5"
          class="mb-4"
        />
        <v-select
          v-model="sectionId"
          :items="sections"
          item-title="name"
          item-value="id"
          label="Section (optional)"
          variant="outlined"
          clearable
          class="mb-4"
        />

        <v-card v-if="parsedEmails.length" variant="tonal" class="mb-4 pa-3">
          <p class="text-caption text-medium-emphasis mb-1">Preview — {{ parsedEmails.length }} email(s):</p>
          <p class="text-body-2">{{ parsedEmails.join(', ') }}</p>
        </v-card>

        <v-btn color="primary" :loading="loading" :disabled="!parsedEmails.length" @click="sendInvitations">
          Send Invitations
        </v-btn>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useStudentStore } from '@/stores/studentStore'
import { sectionApi } from '@/apis/sectionApi'

const studentStore = useStudentStore()

const emailInput = ref('')
const sectionId = ref<number | undefined>(undefined)
const sections = ref<any[]>([])
const loading = ref(false)
const error = ref('')
const successMsg = ref('')

const parsedEmails = computed(() =>
  emailInput.value
    .split(';')
    .map((e) => e.trim())
    .filter((e) => e.includes('@'))
)

onMounted(async () => {
  const res = await sectionApi.search()
  sections.value = res.data.data
})

async function sendInvitations() {
  loading.value = true
  error.value = ''
  successMsg.value = ''
  try {
    const result = await studentStore.inviteStudents(parsedEmails.value, sectionId.value)
    successMsg.value = result.message ?? 'Invitations sent successfully.'
    emailInput.value = ''
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? 'Failed to send invitations'
  } finally {
    loading.value = false
  }
}
</script>
