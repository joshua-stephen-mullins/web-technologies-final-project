<template>
  <v-app>
    <AppTopNav v-if="isAuthenticated" @open-command="commandOpen = true" />
    <CommandPalette v-if="isAuthenticated" v-model="commandOpen" />
    <v-main>
      <router-view />
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import AppTopNav from '@/components/layout/AppTopNav.vue'
import CommandPalette from '@/components/CommandPalette.vue'
import { useAuthStore } from '@/stores/authStore'

const authStore = useAuthStore()
const isAuthenticated = computed(() => authStore.isAuthenticated)
const commandOpen = ref(false)

function handleKeydown(e: KeyboardEvent) {
  if ((e.metaKey || e.ctrlKey) && e.key === 'k') {
    e.preventDefault()
    if (isAuthenticated.value) commandOpen.value = true
  }
}

onMounted(() => window.addEventListener('keydown', handleKeydown))
onUnmounted(() => window.removeEventListener('keydown', handleKeydown))
</script>
