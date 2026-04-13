<template>
  <v-dialog v-model="dialog" max-width="400">
    <v-card>
      <v-card-title>{{ title }}</v-card-title>
      <v-card-text>{{ message }}</v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn @click="cancel">Cancel</v-btn>
        <v-btn color="error" @click="confirm">Confirm</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
// Shared - reusable confirmation dialog used for deletes, deactivations, etc.
// TODO: Implement props (title, message) and emits (confirmed, cancelled)
const dialog = true
const title = 'Confirm'
const message = 'Are you sure?'
const cancel = () => {}
const confirm = () => {}
</script>
