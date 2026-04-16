<template>
  <v-container class="d-flex align-center justify-center" style="min-height: 100vh">
    <v-card width="420" elevation="4">
      <v-card-title class="text-h5 pa-6 pb-2">Forgot Password</v-card-title>
      <v-card-text class="pa-6">
        <v-alert v-if="sent" type="success" class="mb-4">
          If that email is registered, a reset link has been sent.
        </v-alert>
        <v-form v-if="!sent" @submit.prevent="submit">
          <p class="text-body-2 mb-4 text-medium-emphasis">
            Enter your email address and we'll send you a link to reset your password.
          </p>
          <v-text-field
            v-model="email"
            label="Email"
            type="email"
            prepend-inner-icon="mdi-email"
            variant="outlined"
            class="mb-4"
          />
          <v-btn type="submit" color="primary" block size="large" :loading="loading">
            Send Reset Link
          </v-btn>
        </v-form>
        <div class="text-center mt-4">
          <router-link to="/login" class="text-caption text-primary">Back to Login</router-link>
        </div>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { userApi } from '@/apis/userApi'

const email = ref('')
const loading = ref(false)
const sent = ref(false)

async function submit() {
  if (!email.value) return
  loading.value = true
  try {
    await userApi.forgotPassword(email.value)
    sent.value = true
  } finally {
    loading.value = false
  }
}
</script>
