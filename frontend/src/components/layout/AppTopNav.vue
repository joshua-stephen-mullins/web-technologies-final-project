<template>
  <v-app-bar flat height="52">
    <div class="nav-inner">
      <!-- Brand -->
      <div class="nav-brand">
        <div class="brand-mark">
          <svg width="18" height="18" viewBox="0 0 18 18" fill="none">
            <path d="M2 9 Q5 2 9 9 Q13 16 16 9" stroke="#0A84FF" stroke-width="2.5"
                  stroke-linecap="round" stroke-linejoin="round" fill="none"/>
          </svg>
        </div>
        <span class="brand-text">Pulse</span>
      </div>

      <!-- Nav links -->
      <nav class="nav-links">
        <RouterLink to="/" active-class="" exact-active-class="router-link-active" class="top-nav-link">Dashboard</RouterLink>

        <template v-if="auth.isAdmin">
          <RouterLink to="/rubrics"     class="top-nav-link">Rubrics</RouterLink>
          <RouterLink to="/sections"    class="top-nav-link">Sections</RouterLink>
          <RouterLink to="/teams"       class="top-nav-link">Teams</RouterLink>
          <RouterLink to="/students"    class="top-nav-link">Students</RouterLink>
          <RouterLink to="/instructors" class="top-nav-link">Instructors</RouterLink>
        </template>

        <template v-else-if="auth.isInstructor">
          <RouterLink to="/teams"                      class="top-nav-link">My Teams</RouterLink>
          <RouterLink to="/students"                   class="top-nav-link">Students</RouterLink>
          <RouterLink to="/evaluations/section-report" class="top-nav-link">Section Report</RouterLink>
          <RouterLink to="/activities/report"          class="top-nav-link">WAR Reports</RouterLink>
        </template>

        <template v-else-if="auth.isStudent">
          <RouterLink to="/activities"           class="top-nav-link">My WAR</RouterLink>
          <RouterLink to="/evaluations/submit"   class="top-nav-link">Peer Eval</RouterLink>
          <RouterLink to="/evaluations/my-report" class="top-nav-link">My Report</RouterLink>
        </template>
      </nav>

      <!-- Right actions -->
      <div class="nav-actions">
        <!-- ⌘K button -->
        <button class="cmd-btn" @click="$emit('open-command')">
          <v-icon size="14" style="opacity:0.6">mdi-magnify</v-icon>
          <span class="cmd-label">Search</span>
          <kbd class="cmd-kbd">⌘K</kbd>
        </button>

        <!-- User menu -->
        <v-menu location="bottom end" :offset="8">
          <template #activator="{ props }">
            <button v-bind="props" class="user-btn">
              <v-avatar size="26" color="primary">
                <span style="font-size:11px; font-weight:700">{{ initials }}</span>
              </v-avatar>
            </button>
          </template>
          <v-list rounded="xl" elevation="4" min-width="180" class="pa-1" density="compact"
                  style="background:#1C1C1F; border:1px solid rgba(255,255,255,0.08)">
            <div class="px-3 py-2" style="border-bottom:1px solid rgba(255,255,255,0.07)">
              <div style="font-size:13px; font-weight:600; color:#F5F5F7">{{ auth.username }}</div>
              <div style="font-size:11px; color:rgba(255,255,255,0.4); margin-top:1px">{{ roleLabel }}</div>
            </div>
            <v-list-item prepend-icon="mdi-account-outline" title="Profile" to="/profile"
                         rounded="lg" class="mt-1" />
            <v-divider class="my-1" />
            <v-list-item prepend-icon="mdi-arrow-right-circle-outline" title="Sign Out"
                         rounded="lg" base-color="error" @click="auth.logout()" />
          </v-list>
        </v-menu>
      </div>
    </div>
  </v-app-bar>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

defineEmits<{ (e: 'open-command'): void }>()
const auth = useAuthStore()

const initials = computed(() => {
  const name = auth.username ?? ''
  const parts = name.split('@')[0].split('.')
  return parts.map((p: string) => p[0]?.toUpperCase() ?? '').join('').slice(0, 2)
})

const roleLabel = computed(() => {
  if (auth.isAdmin) return 'Administrator'
  if (auth.isInstructor) return 'Instructor'
  if (auth.isStudent) return 'Student'
  return ''
})
</script>

<style scoped>
.nav-inner {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 0 16px;
  gap: 8px;
}
.nav-brand {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-right: 8px;
  flex-shrink: 0;
}
.brand-mark {
  width: 28px;
  height: 28px;
  background: rgba(10, 132, 255, 0.12);
  border: 1px solid rgba(10, 132, 255, 0.25);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.brand-text {
  font-size: 15px;
  font-weight: 700;
  letter-spacing: -0.4px;
  color: #F5F5F7;
}
.nav-links {
  display: flex;
  align-items: center;
  gap: 2px;
  flex: 1;
  min-width: 0;
  overflow: hidden;
}
.nav-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
  margin-left: auto;
}
.cmd-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 5px 14px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 8px;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.5);
  font-family: inherit;
  font-size: 13px;
  min-width: 140px;
  transition: all 0.15s ease;
}
.cmd-btn:hover {
  background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.8);
}
.cmd-label { font-size: 13px; }
.cmd-kbd {
  font-family: inherit;
  font-size: 11px;
  padding: 1px 5px;
  background: rgba(255, 255, 255, 0.07);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  color: rgba(255, 255, 255, 0.4);
}
.user-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 2px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  transition: opacity 0.15s ease;
}
.user-btn:hover { opacity: 0.8; }
</style>
