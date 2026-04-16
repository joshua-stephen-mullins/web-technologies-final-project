<template>
  <v-container class="d-flex align-center justify-center" style="min-height: 100vh">
    <v-card width="460" elevation="4">
      <v-card-title class="text-h5 pa-6 pb-2">Set Up Your Account</v-card-title>
      <v-card-text class="pa-6">
        <v-alert v-if="!token" type="error">
          Invalid or missing invitation link.
        </v-alert>
        <v-alert v-if="error" type="error" class="mb-4" closable @click:close="error = ''">
          {{ error }}
        </v-alert>
        <v-alert v-if="success" type="success">
          Account created! <router-link to="/login">Sign in</router-link>
        </v-alert>
        <v-form v-if="token && !success" @submit.prevent="submit">
          <v-text-field v-model="form.firstName" label="First Name" variant="outlined" class="mb-3" />
          <v-text-field v-model="form.lastName" label="Last Name" variant="outlined" class="mb-3" />
          <v-text-field
            v-model="form.password"
            label="Password"
            :type="showPassword ? 'text' : 'password'"
            :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
            variant="outlined"
            hint="At least 8 characters"
            class="mb-4"
            @click:append-inner="showPassword = !showPassword"
          />
          <v-btn type="submit" color="primary" block size="large" :loading="loading">
            Create Account
          </v-btn>
        </v-form>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { userApi } from '@/apis/userApi'

const route = useRoute()
const token = route.query.token as string | undefined

const form = ref({ firstName: '', lastName: '', password: '' })
const showPassword = ref(false)
const loading = ref(false)
const error = ref('')
const success = ref(false)

async function submit() {
  loading.value = true
  error.value = ''
  try {
    await userApi.register(token!, form.value)
    success.value = true
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? 'Registration failed'
  } finally {
    loading.value = false
  }
}
</script>
