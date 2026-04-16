<template>
  <v-container class="pa-6" max-width="640">
    <h1 class="text-h4 mb-6">My Profile</h1>

    <v-alert v-if="profileSuccess" type="success" class="mb-4" closable @click:close="profileSuccess = false">
      Profile updated.
    </v-alert>
    <v-alert v-if="profileError" type="error" class="mb-4" closable @click:close="profileError = ''">
      {{ profileError }}
    </v-alert>

    <v-card class="mb-6">
      <v-card-title class="pa-4">Account Info</v-card-title>
      <v-card-text>
        <v-form @submit.prevent="saveProfile">
          <v-text-field v-model="profile.firstName" label="First Name" variant="outlined" class="mb-3" />
          <v-text-field v-model="profile.lastName" label="Last Name" variant="outlined" class="mb-3" />
          <v-text-field v-model="profile.email" label="Email" type="email" variant="outlined" class="mb-4" />
          <v-btn type="submit" color="primary" :loading="profileLoading">Save Changes</v-btn>
        </v-form>
      </v-card-text>
    </v-card>

    <v-card>
      <v-card-title class="pa-4">Change Password</v-card-title>
      <v-card-text>
        <v-alert v-if="pwSuccess" type="success" class="mb-3" closable @click:close="pwSuccess = false">
          Password changed.
        </v-alert>
        <v-alert v-if="pwError" type="error" class="mb-3" closable @click:close="pwError = ''">
          {{ pwError }}
        </v-alert>
        <v-form @submit.prevent="savePassword">
          <v-text-field
            v-model="pw.oldPassword"
            label="Current Password"
            type="password"
            variant="outlined"
            class="mb-3"
          />
          <v-text-field
            v-model="pw.newPassword"
            label="New Password"
            type="password"
            variant="outlined"
            hint="At least 8 characters"
            class="mb-3"
          />
          <v-text-field
            v-model="pw.confirm"
            label="Confirm New Password"
            type="password"
            variant="outlined"
            :error-messages="pw.confirm && pw.confirm !== pw.newPassword ? ['Passwords do not match'] : []"
            class="mb-4"
          />
          <v-btn
            type="submit"
            color="primary"
            :loading="pwLoading"
            :disabled="!pw.oldPassword || !pw.newPassword || pw.newPassword !== pw.confirm"
          >
            Change Password
          </v-btn>
        </v-form>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { userApi } from '@/apis/userApi'

const profile = ref({ firstName: '', lastName: '', email: '' })
const profileLoading = ref(false)
const profileSuccess = ref(false)
const profileError = ref('')

const pw = ref({ oldPassword: '', newPassword: '', confirm: '' })
const pwLoading = ref(false)
const pwSuccess = ref(false)
const pwError = ref('')

onMounted(async () => {
  const res = await userApi.getMe()
  const user = res.data.data
  profile.value = { firstName: user.firstName, lastName: user.lastName, email: user.username }
})

async function saveProfile() {
  profileLoading.value = true
  profileError.value = ''
  try {
    await userApi.updateMe(profile.value)
    profileSuccess.value = true
  } catch (e: any) {
    profileError.value = e?.response?.data?.message ?? 'Update failed'
  } finally {
    profileLoading.value = false
  }
}

async function savePassword() {
  pwLoading.value = true
  pwError.value = ''
  try {
    await userApi.changePassword({ oldPassword: pw.value.oldPassword, newPassword: pw.value.newPassword })
    pwSuccess.value = true
    pw.value = { oldPassword: '', newPassword: '', confirm: '' }
  } catch (e: any) {
    pwError.value = e?.response?.data?.message ?? 'Password change failed'
  } finally {
    pwLoading.value = false
  }
}
</script>
