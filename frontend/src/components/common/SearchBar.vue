<template>
  <v-text-field
    v-model="query"
    label="Search"
    prepend-inner-icon="mdi-magnify"
    clearable
    @update:model-value="$emit('search', query)"
  />
</template>

<script setup lang="ts">
// Shared - reusable search input used on list pages (UC-2,7,15,21)
import { ref } from 'vue'
const query = ref('')
defineEmits(['search'])
</script>
