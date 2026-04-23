<template>
  <v-container class="pa-6" max-width="760">
    <!-- Header -->
    <div class="d-flex align-center gap-3 mb-6">
      <v-btn icon="mdi-arrow-left" variant="text" to="/teams" />
      <div class="flex-1">
        <h1 class="text-h5 font-weight-bold">{{ team?.name ?? 'Loading…' }}</h1>
        <p v-if="team?.description" class="text-caption text-medium-emphasis mb-0">{{ team.description }}</p>
      </div>
      <div v-if="authStore.isAdmin" class="d-flex gap-2">
        <v-btn variant="tonal" size="small" :to="`/teams/${route.params.id}/edit`">Edit</v-btn>
        <v-btn variant="tonal" color="error" size="small" @click="confirmDelete = true">Delete</v-btn>
      </div>
    </div>

    <v-alert v-if="error" type="error" class="mb-4">{{ error }}</v-alert>

    <div v-if="loading" class="d-flex justify-center pa-8">
      <v-progress-circular indeterminate color="primary" />
    </div>

    <template v-else-if="team">
      <!-- Details card -->
      <v-card class="mb-4">
        <v-card-text>
          <div v-if="team.websiteUrl" class="mb-2">
            <span class="text-caption text-medium-emphasis">Website: </span>
            <a :href="team.websiteUrl" target="_blank" class="text-primary">{{ team.websiteUrl }}</a>
          </div>
        </v-card-text>
      </v-card>

      <!-- Students -->
      <v-card class="mb-4">
        <v-card-title class="d-flex align-center justify-space-between pa-4 pb-2">
          <span>Students ({{ team.students?.length ?? 0 }})</span>
          <v-btn v-if="authStore.isAdmin" size="small" variant="tonal" prepend-icon="mdi-account-plus"
                 @click="showAssignStudent = true">
            Assign Student
          </v-btn>
        </v-card-title>
        <v-card-text class="pa-2">
          <div v-if="!team.students?.length" class="pa-4 text-center text-medium-emphasis text-caption">No students assigned.</div>
          <v-list v-else density="compact">
            <v-list-item
              v-for="student in team.students"
              :key="student.id"
              :title="student.firstName + ' ' + student.lastName"
              :to="`/students/${student.id}`"
            >
              <template v-if="authStore.isAdmin" #append>
                <v-btn icon="mdi-account-remove" size="x-small" variant="text" color="error"
                       @click.prevent="removeStudent(student.id)" />
              </template>
            </v-list-item>
          </v-list>
        </v-card-text>
      </v-card>

      <!-- Instructors (Whitney adds assign/remove here for UC-19/20) -->
      <v-card class="mb-4">
        <v-card-title class="pa-4 pb-2">Instructors ({{ team.instructors?.length ?? 0 }})</v-card-title>
        <v-card-text class="pa-2">
          <div v-if="!team.instructors?.length" class="pa-4 text-center text-medium-emphasis text-caption">No instructors assigned.</div>
          <v-list v-else density="compact">
            <v-list-item
              v-for="instructor in team.instructors"
              :key="instructor.id"
              :title="instructor.firstName + ' ' + instructor.lastName"
            />
          </v-list>
        </v-card-text>
      </v-card>
    </template>

    <!-- Assign student dialog -->
    <v-dialog v-model="showAssignStudent" max-width="480">
      <v-card>
        <v-card-title class="pa-4">Assign Students</v-card-title>
        <v-card-text>
          <v-autocomplete
            v-model="selectedStudentIds"
            :items="allStudents"
            item-title="label"
            item-value="id"
            label="Select students"
            multiple
            chips
            closable-chips
            variant="outlined"
          />
        </v-card-text>
        <v-card-actions class="pa-4 pt-0">
          <v-spacer />
          <v-btn variant="text" @click="showAssignStudent = false">Cancel</v-btn>
          <v-btn color="primary" :loading="assigning" @click="assignStudents">Assign</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Delete confirmation -->
    <v-dialog v-model="confirmDelete" max-width="400">
      <v-card>
        <v-card-title class="pa-4">Delete Team?</v-card-title>
        <v-card-text>This will permanently delete the team and all associated WARs. This cannot be undone.</v-card-text>
        <v-card-actions class="pa-4 pt-0">
          <v-spacer />
          <v-btn variant="text" @click="confirmDelete = false">Cancel</v-btn>
          <v-btn color="error" :loading="deleting" @click="deleteTeam">Delete</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTeamStore } from '@/stores/teamStore'
import { useAuthStore } from '@/stores/authStore'
import { useStudentStore } from '@/stores/studentStore'

const route = useRoute()
const router = useRouter()
const teamStore = useTeamStore()
const authStore = useAuthStore()
const studentStore = useStudentStore()

const loading = ref(false)
const error = ref('')
const confirmDelete = ref(false)
const deleting = ref(false)
const showAssignStudent = ref(false)
const selectedStudentIds = ref<number[]>([])
const assigning = ref(false)

const team = computed(() => teamStore.currentTeam)
const allStudents = computed(() =>
  studentStore.students.map((s: any) => ({ id: s.id, label: `${s.firstName} ${s.lastName} (${s.email})` }))
)

onMounted(async () => {
  loading.value = true
  try {
    await teamStore.fetchTeamById(Number(route.params.id))
    if (authStore.isAdmin) {
      await studentStore.fetchStudents()
    }
  } catch {
    error.value = 'Failed to load team.'
  } finally {
    loading.value = false
  }
})

async function removeStudent(studentId: number) {
  try {
    await teamStore.removeStudent(Number(route.params.id), studentId)
  } catch {
    error.value = 'Failed to remove student.'
  }
}

async function assignStudents() {
  if (!selectedStudentIds.value.length) return
  assigning.value = true
  try {
    await teamStore.assignStudents(Number(route.params.id), selectedStudentIds.value)
    showAssignStudent.value = false
    selectedStudentIds.value = []
  } catch {
    error.value = 'Failed to assign students.'
  } finally {
    assigning.value = false
  }
}

async function deleteTeam() {
  deleting.value = true
  try {
    await teamStore.deleteTeam(Number(route.params.id))
    router.push('/teams')
  } catch {
    error.value = 'Failed to delete team.'
    confirmDelete.value = false
  } finally {
    deleting.value = false
  }
}
</script>
