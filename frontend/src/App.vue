<template>
  <v-app>
    <AppNavbar v-if="isAuthenticated" />
    <AppSidebar v-if="isAuthenticated" />
    <v-main>
      <router-view />
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import AppNavbar from '@/components/layout/AppNavbar.vue'
import AppSidebar from '@/components/layout/AppSidebar.vue'
import { useAuthStore } from '@/stores/authStore'

const authStore = useAuthStore()
const isAuthenticated = computed(() => authStore.isAuthenticated)
</script>
