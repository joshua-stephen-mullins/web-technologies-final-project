<template>
  <v-container class="pa-6" max-width="720">
    <div class="d-flex align-center gap-3 mb-6">
      <v-btn icon="mdi-arrow-left" variant="text" to="/students" />
      <div class="flex-1">
        <h1 class="text-h5 font-weight-bold">
          {{ student ? student.firstName + ' ' + student.lastName : 'Loading…' }}
        </h1>
        <p v-if="student?.teamName" class="text-caption text-medium-emphasis mb-0">
          Team: {{ student.teamName }}{{ student.sectionName ? ' · ' + student.sectionName : '' }}
        </p>
      </div>
      <v-btn v-if="authStore.isAdmin" variant="tonal" color="error" size="small" @click="confirmDelete = true">
        Delete Student
      </v-btn>
    </div>

    <v-alert v-if="error" type="error" class="mb-4">{{ error }}</v-alert>

    <div v-if="loading" class="d-flex justify-center pa-8">
      <v-progress-circular indeterminate color="primary" />
    </div>

    <template v-else-if="student">
      <v-card class="mb-4">
        <v-card-text>
          <v-list density="compact">
            <v-list-item title="Email" :subtitle="student.email" />
            <v-list-item title="Team" :subtitle="student.teamName ?? 'Not assigned'" />
            <v-list-item title="Section" :subtitle="student.sectionName ?? '—'" />
          </v-list>
        </v-card-text>
      </v-card>

      <!-- Instructor actions -->
      <div v-if="authStore.isInstructor || authStore.isAdmin" class="d-flex gap-3">
        <v-btn variant="tonal" size="small" prepend-icon="mdi-chart-bar"
               :to="`/evaluations/student-report/${student.id}`">
          Evaluation Report
        </v-btn>
        <v-btn variant="tonal" size="small" prepend-icon="mdi-clipboard-text"
               :to="`/evaluations/war-report/${student.id}`">
          WAR Report
        </v-btn>
      </div>
    </template>

    <!-- Delete confirmation -->
    <v-dialog v-model="confirmDelete" max-width="400">
      <v-card>
        <v-card-title class="pa-4">Delete Student?</v-card-title>
        <v-card-text>This will permanently delete the student and all their WARs and peer evaluations. This cannot be undone.</v-card-text>
        <v-card-actions class="pa-4 pt-0">
          <v-spacer />
          <v-btn variant="text" @click="confirmDelete = false">Cancel</v-btn>
          <v-btn color="error" :loading="deleting" @click="deleteStudent">Delete</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStudentStore } from '@/stores/studentStore'
import { useAuthStore } from '@/stores/authStore'

const route = useRoute()
const router = useRouter()
const studentStore = useStudentStore()
const authStore = useAuthStore()

const loading = ref(false)
const error = ref('')
const confirmDelete = ref(false)
const deleting = ref(false)

const student = computed(() => studentStore.currentStudent)

onMounted(async () => {
  loading.value = true
  try {
    await studentStore.fetchStudentById(Number(route.params.id))
  } catch {
    error.value = 'Failed to load student.'
  } finally {
    loading.value = false
  }
})

async function deleteStudent() {
  deleting.value = true
  try {
    await studentStore.deleteStudent(Number(route.params.id))
    router.push('/students')
  } catch {
    error.value = 'Failed to delete student.'
    confirmDelete.value = false
  } finally {
    deleting.value = false
  }
}
</script>
