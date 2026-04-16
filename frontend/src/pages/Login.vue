<template>
  <div class="login-page">
    <div class="login-wrapper">
      <!-- Brand -->
      <div class="brand mb-8">
        <div class="brand-icon mb-4">
          <v-icon size="32" color="white">mdi-pulse</v-icon>
        </div>
        <h1 class="brand-title">Project Pulse</h1>
        <p class="brand-sub">Senior Design Peer Evaluation System</p>
      </div>

      <!-- Card -->
      <v-card class="login-card" width="400" elevation="0">
        <v-card-text class="pa-8">
          <h2 class="login-heading mb-1">Sign In</h2>
          <p class="login-sub mb-6">Enter your credentials to continue</p>

          <v-alert
            v-if="error"
            type="error"
            variant="tonal"
            closable
            class="mb-5"
            @click:close="error = ''"
          >
            {{ error }}
          </v-alert>

          <v-form @submit.prevent="submit">
            <v-text-field
              v-model="username"
              label="Email Address"
              type="email"
              prepend-inner-icon="mdi-email-outline"
              class="mb-3"
              autofocus
            />
            <v-text-field
              v-model="password"
              label="Password"
              :type="showPassword ? 'text' : 'password'"
              prepend-inner-icon="mdi-lock-outline"
              :append-inner-icon="showPassword ? 'mdi-eye-off-outline' : 'mdi-eye-outline'"
              class="mb-2"
              @click:append-inner="showPassword = !showPassword"
            />

            <div class="text-right mb-5">
              <router-link to="/forgot-password" class="forgot-link">
                Forgot password?
              </router-link>
            </div>

            <v-btn
              type="submit"
              color="primary"
              block
              size="large"
              rounded="xl"
              :loading="loading"
              style="font-size:15px; font-weight:600; height:48px"
            >
              Sign In
            </v-btn>
          </v-form>
        </v-card-text>
      </v-card>

      <p class="login-footer">TCU Senior Design · Spring 2026</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '@/stores/authStore'

const authStore = useAuthStore()
const username = ref('')
const password = ref('')
const showPassword = ref(false)
const loading = ref(false)
const error = ref('')

async function submit() {
  if (!username.value || !password.value) return
  loading.value = true
  error.value = ''
  try {
    await authStore.login(username.value, password.value)
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? 'Invalid credentials'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: #0C0C0E;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}
.login-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 400px;
}
.brand {
  text-align: center;
}
.brand-icon {
  width: 72px;
  height: 72px;
  border-radius: 20px;
  background: linear-gradient(145deg, #0A84FF 0%, #30D158 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  box-shadow: 0 12px 32px rgba(10, 132, 255, 0.28);
}
.brand-title {
  font-size: 26px;
  font-weight: 700;
  letter-spacing: -0.5px;
  color: #F5F5F7;
  margin: 0;
}
.brand-sub {
  font-size: 14px;
  color: rgba(255,255,255,0.4);
  margin: 4px 0 0;
}
.login-card {
  background: #161618 !important;
  border: 1px solid rgba(255,255,255,0.08) !important;
  box-shadow: 0 24px 60px rgba(0,0,0,0.4) !important;
  width: 100%;
}
.login-heading {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: -0.4px;
  color: #F5F5F7;
}
.login-sub {
  font-size: 14px;
  color: rgba(255,255,255,0.4);
}
.forgot-link {
  font-size: 13px;
  color: #0A84FF;
  text-decoration: none;
  font-weight: 500;
}
.forgot-link:hover {
  text-decoration: underline;
}
.login-footer {
  font-size: 12px;
  color: rgba(255,255,255,0.2);
  margin-top: 24px;
}
</style>
