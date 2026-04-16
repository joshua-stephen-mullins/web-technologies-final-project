<template>
  <v-navigation-drawer v-model="drawer" width="240">
    <div class="sidebar-top pt-3 pb-1 px-3">
      <div class="text-caption font-weight-bold" style="color:rgba(0,0,0,0.3); letter-spacing:0.06em; text-transform:uppercase; font-size:10px">
        {{ roleLabel }}
      </div>
    </div>

    <v-list nav density="compact" class="px-2">
      <v-list-item
        prepend-icon="mdi-square-rounded-outline"
        title="Dashboard"
        to="/"
        exact
        rounded="lg"
      />

      <template v-if="authStore.isAdmin">
        <div class="nav-section-label">Manage</div>
        <v-list-item prepend-icon="mdi-book-open-outline"   title="Rubrics"     to="/rubrics"     rounded="lg" />
        <v-list-item prepend-icon="mdi-layers-outline"      title="Sections"    to="/sections"    rounded="lg" />
        <v-list-item prepend-icon="mdi-account-group-outline" title="Teams"     to="/teams"       rounded="lg" />
        <v-list-item prepend-icon="mdi-school-outline"      title="Students"    to="/students"    rounded="lg" />
        <v-list-item prepend-icon="mdi-account-tie-outline" title="Instructors" to="/instructors" rounded="lg" />
      </template>

      <template v-if="authStore.isInstructor">
        <div class="nav-section-label">Teaching</div>
        <v-list-item prepend-icon="mdi-account-group-outline" title="My Teams"       to="/teams"                       rounded="lg" />
        <v-list-item prepend-icon="mdi-school-outline"        title="Students"       to="/students"                    rounded="lg" />
        <v-list-item prepend-icon="mdi-chart-bar-stacked"     title="Section Report" to="/evaluations/section-report"  rounded="lg" />
        <v-list-item prepend-icon="mdi-file-chart-outline"    title="WAR Reports"    to="/activities/report"           rounded="lg" />
      </template>

      <template v-if="authStore.isStudent">
        <div class="nav-section-label">My Work</div>
        <v-list-item prepend-icon="mdi-clipboard-list-outline" title="My WAR"        to="/activities"              rounded="lg" />
        <v-list-item prepend-icon="mdi-star-outline"           title="Peer Eval"     to="/evaluations/submit"      rounded="lg" />
        <v-list-item prepend-icon="mdi-chart-line-variant"     title="My Report"     to="/evaluations/my-report"   rounded="lg" />
      </template>
    </v-list>

    <template #append>
      <div class="px-2 pb-3">
        <v-divider class="mb-2" />
        <v-list nav density="compact">
          <v-list-item prepend-icon="mdi-account-outline" title="Profile" to="/profile" rounded="lg" />
        </v-list>
      </div>
    </template>
  </v-navigation-drawer>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAuthStore } from '@/stores/authStore'

const authStore = useAuthStore()
const drawer = defineModel<boolean>('drawer', { default: true })

const roleLabel = computed(() => {
  if (authStore.isAdmin) return 'Administrator'
  if (authStore.isInstructor) return 'Instructor'
  if (authStore.isStudent) return 'Student'
  return 'Navigation'
})
</script>

<style scoped>
.nav-section-label {
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.07em;
  text-transform: uppercase;
  color: rgba(0, 0, 0, 0.32);
  padding: 12px 10px 4px;
}
</style>
