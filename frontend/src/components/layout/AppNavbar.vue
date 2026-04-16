<template>
  <v-app-bar flat height="56">
    <v-btn
      icon
      size="small"
      variant="text"
      class="ml-1"
      @click="$emit('toggle-drawer')"
    >
      <v-icon size="20">mdi-menu</v-icon>
    </v-btn>

    <v-app-bar-title>
      <div class="d-flex align-center ga-2">
        <div class="brand-dot" />
        <span style="font-size:16px; font-weight:700; letter-spacing:-0.4px; color:#1D1D1F">
          Project Pulse
        </span>
      </div>
    </v-app-bar-title>

    <v-spacer />

    <v-menu location="bottom end" :offset="8">
      <template #activator="{ props }">
        <v-btn v-bind="props" variant="text" rounded="xl" class="mr-2 user-menu-btn" size="small">
          <v-avatar size="26" color="primary" class="mr-2">
            <span class="text-caption font-weight-bold text-white" style="font-size:11px">
              {{ initials }}
            </span>
          </v-avatar>
          <span style="font-size:13px; font-weight:500; color:#1D1D1F; max-width:140px"
                class="text-truncate">
            {{ authStore.username }}
          </span>
          <v-icon size="14" class="ml-1" style="color:#8E8E93">mdi-chevron-down</v-icon>
        </v-btn>
      </template>

      <v-list elevation="2" rounded="xl" min-width="180" class="pa-1" density="compact">
        <v-list-item
          prepend-icon="mdi-account-outline"
          title="Profile"
          to="/profile"
          rounded="lg"
        />
        <v-divider class="my-1" />
        <v-list-item
          prepend-icon="mdi-arrow-right-circle-outline"
          title="Sign Out"
          rounded="lg"
          base-color="error"
          @click="authStore.logout()"
        />
      </v-list>
    </v-menu>
  </v-app-bar>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAuthStore } from '@/stores/authStore'

defineEmits<{ (e: 'toggle-drawer'): void }>()
const authStore = useAuthStore()

const initials = computed(() => {
  const name = authStore.username ?? ''
  const parts = name.split('@')[0].split('.')
  return parts.map((p: string) => p[0]?.toUpperCase() ?? '').join('').slice(0, 2)
})
</script>

<style scoped>
.brand-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: linear-gradient(135deg, #007AFF 0%, #34C759 100%);
  flex-shrink: 0;
}
.user-menu-btn {
  background: rgba(0, 0, 0, 0.04) !important;
  border: 1px solid rgba(0, 0, 0, 0.07) !important;
  padding: 0 10px !important;
  height: 34px !important;
}
</style>
