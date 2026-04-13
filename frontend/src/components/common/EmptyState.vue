<template>
  <v-container class="text-center pa-8">
    <v-icon size="64" color="grey-lighten-1">mdi-inbox-outline</v-icon>
    <p class="text-h6 mt-4 text-grey">{{ message }}</p>
    <slot />
  </v-container>
</template>

<script setup lang="ts">
// Shared - shown when a search returns no results
defineProps<{ message: string }>()
</script>
