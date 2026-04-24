<template>
  <div class="register-page">
    <div class="register-wrapper">
      <!-- Brand -->
      <div class="brand mb-8">
        <div class="brand-icon mb-4">
          <v-icon size="32" color="white">mdi-pulse</v-icon>
        </div>
        <h1 class="brand-title">Project Pulse</h1>
        <p class="brand-sub">Senior Design Peer Evaluation System</p>
      </div>

      <!-- Invalid / missing token -->
      <v-card v-if="tokenState === 'invalid'" class="reg-card" elevation="0">
        <v-card-text class="pa-8">
          <p class="reg-heading mb-2">Invalid Invitation Link</p>
          <p class="reg-sub mb-6">This link is invalid or has expired. Please contact your administrator.</p>
          <v-btn color="primary" block rounded="xl" to="/login">Go to Login</v-btn>
        </v-card-text>
      </v-card>

      <!-- Extension 2a: token already used -->
      <v-card v-else-if="tokenState === 'used'" class="reg-card" elevation="0">
        <v-card-text class="pa-8">
          <p class="reg-heading mb-2">Account Already Set Up</p>
          <p class="reg-sub mb-6">You have already created your account. Please log in.</p>
          <v-btn color="primary" block rounded="xl" to="/login">Go to Login</v-btn>
        </v-card-text>
      </v-card>

      <!-- Loading -->
      <v-card v-else-if="tokenState === 'loading'" class="reg-card" elevation="0">
        <v-card-text class="pa-8 text-center">
          <p class="reg-sub">Validating invitation link...</p>
        </v-card-text>
      </v-card>

      <!-- Step 1: Enter details -->
      <v-card v-else-if="step === 1" class="reg-card" elevation="0">
        <v-card-text class="pa-8">
          <p class="reg-heading mb-1">Set Up Your Account</p>
          <p class="reg-sub mb-6">Enter your details to complete registration</p>

          <v-alert v-if="formError" type="error" variant="tonal" closable class="mb-5" @click:close="formError = ''">
            {{ formError }}
          </v-alert>

          <div class="field-group mb-4">
            <label class="field-label">Email Address</label>
            <input :value="invitedEmail" class="field-input" disabled />
          </div>

          <div class="field-group mb-4">
            <label class="field-label">First Name</label>
            <input v-model="form.firstName" class="field-input" placeholder="Enter your first name" @keyup.enter="goToReview" />
          </div>

          <div class="field-group mb-4">
            <label class="field-label">Middle Initial <span class="field-optional">(optional)</span></label>
            <input v-model="form.middleInitial" class="field-input" placeholder="e.g. A" maxlength="1" @keyup.enter="goToReview" style="max-width: 80px" />
          </div>

          <div class="field-group mb-4">
            <label class="field-label">Last Name</label>
            <input v-model="form.lastName" class="field-input" placeholder="Enter your last name" @keyup.enter="goToReview" />
          </div>

          <div class="field-group mb-4">
            <label class="field-label">Password</label>
            <div class="password-wrapper">
              <input
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                class="field-input"
                placeholder="At least 8 characters"
                @keyup.enter="goToReview"
              />
              <button type="button" class="pw-toggle" @click="showPassword = !showPassword" tabindex="-1">
                <v-icon size="18" color="rgba(255,255,255,0.4)">
                  {{ showPassword ? 'mdi-eye-off-outline' : 'mdi-eye-outline' }}
                </v-icon>
              </button>
            </div>
          </div>

          <div class="field-group mb-6">
            <label class="field-label">Re-enter Password</label>
            <div class="password-wrapper">
              <input
                v-model="form.confirmPassword"
                :type="showConfirmPassword ? 'text' : 'password'"
                class="field-input"
                placeholder="Re-enter your password"
                @keyup.enter="goToReview"
              />
              <button type="button" class="pw-toggle" @click="showConfirmPassword = !showConfirmPassword" tabindex="-1">
                <v-icon size="18" color="rgba(255,255,255,0.4)">
                  {{ showConfirmPassword ? 'mdi-eye-off-outline' : 'mdi-eye-outline' }}
                </v-icon>
              </button>
            </div>
          </div>

          <v-btn color="primary" block size="large" rounded="xl" @click="goToReview"
            style="font-size:15px; font-weight:600; height:48px">
            Review &amp; Confirm
          </v-btn>
        </v-card-text>
      </v-card>

      <!-- Step 2: Review and confirm -->
      <v-card v-else-if="step === 2" class="reg-card" elevation="0">
        <v-card-text class="pa-8">
          <p class="reg-heading mb-1">Confirm Your Details</p>
          <p class="reg-sub mb-6">Please review your account information before creating your account</p>

          <div class="review-table mb-6">
            <div class="review-row">
              <span class="review-label">Email</span>
              <span class="review-value">{{ invitedEmail }}</span>
            </div>
            <div class="review-row">
              <span class="review-label">First Name</span>
              <span class="review-value">{{ form.firstName }}</span>
            </div>
            <div class="review-row" v-if="form.middleInitial">
              <span class="review-label">Middle Initial</span>
              <span class="review-value">{{ form.middleInitial.toUpperCase() }}</span>
            </div>
            <div class="review-row">
              <span class="review-label">Last Name</span>
              <span class="review-value">{{ form.lastName }}</span>
            </div>
            <div class="review-row">
              <span class="review-label">Password</span>
              <span class="review-value">••••••••</span>
            </div>
          </div>

          <v-alert v-if="submitError" type="error" variant="tonal" closable class="mb-5" @click:close="submitError = ''">
            {{ submitError }}
          </v-alert>

          <div class="confirm-actions">
            <v-btn variant="text" @click="step = 1">Edit Details</v-btn>
            <v-btn color="primary" rounded="xl" size="large" :loading="submitting" @click="submit"
              style="font-size:15px; font-weight:600; height:48px; flex:1">
              Create Account
            </v-btn>
          </div>
        </v-card-text>
      </v-card>

      <p class="reg-footer">TCU Senior Design · Spring 2026</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { userApi } from '@/apis/userApi'

// UC-25: Student sets up account via invitation link
// UC-30: Instructor registration uses the same flow

const route = useRoute()
const router = useRouter()

const token = route.query.token as string | undefined

type TokenState = 'loading' | 'valid' | 'used' | 'invalid'
const tokenState = ref<TokenState>('loading')
const invitedEmail = ref('')
const step = ref(1)

const form = ref({ firstName: '', middleInitial: '', lastName: '', password: '', confirmPassword: '' })
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const formError = ref('')
const submitError = ref('')
const submitting = ref(false)

onMounted(async () => {
  if (!token) {
    tokenState.value = 'invalid'
    return
  }
  try {
    const res = await userApi.validateToken(token)
    if (res.data.flag) {
      invitedEmail.value = res.data.data.email
      tokenState.value = 'valid'
    } else if (res.data.message === 'already_used') {
      tokenState.value = 'used'
    } else {
      tokenState.value = 'invalid'
    }
  } catch {
    tokenState.value = 'invalid'
  }
})

function goToReview() {
  formError.value = ''
  if (!form.value.firstName.trim()) { formError.value = 'First name is required.'; return }
  if (form.value.middleInitial && !/^[A-Za-z]$/.test(form.value.middleInitial)) { formError.value = 'Middle initial must be a single letter.'; return }
  if (!form.value.lastName.trim()) { formError.value = 'Last name is required.'; return }
  if (form.value.password.length < 8) { formError.value = 'Password must be at least 8 characters.'; return }
  if (form.value.password !== form.value.confirmPassword) { formError.value = 'Passwords do not match.'; return }
  step.value = 2
}

async function submit() {
  submitting.value = true
  submitError.value = ''
  try {
    await userApi.register(token!, {
      firstName: form.value.firstName.trim(),
      middleInitial: form.value.middleInitial.trim() || null,
      lastName: form.value.lastName.trim(),
      password: form.value.password,
    })
    router.push('/login?registered=true')
  } catch (e: any) {
    submitError.value = e?.response?.data?.message ?? 'Registration failed. Please try again.'
    step.value = 1
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background: #0C0C0E;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.register-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 420px;
}

.brand { text-align: center; }

.brand-icon {
  width: 72px;
  height: 72px;
  border-radius: 20px;
  background: linear-gradient(145deg, #0A84FF 0%, #30D158 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  box-shadow: 0 12px 32px rgba(10,132,255,0.28);
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

.reg-card {
  background: #161618 !important;
  border: 1px solid rgba(255,255,255,0.08) !important;
  box-shadow: 0 24px 60px rgba(0,0,0,0.4) !important;
  width: 100%;
  border-radius: 20px !important;
}

.reg-heading {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: -0.4px;
  color: #F5F5F7;
  margin: 0;
}

.reg-sub {
  font-size: 14px;
  color: rgba(255,255,255,0.4);
  margin: 0;
}

.field-group { display: flex; flex-direction: column; gap: 6px; }

.field-label {
  font-size: 12px;
  font-weight: 500;
  color: rgba(255,255,255,0.45);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.field-optional {
  font-size: 11px;
  font-weight: 400;
  text-transform: none;
  letter-spacing: 0;
  color: rgba(255,255,255,0.25);
}

.field-input {
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 10px;
  padding: 12px 14px;
  font-size: 15px;
  color: #F5F5F7;
  outline: none;
  transition: border-color 0.15s;
  width: 100%;
  box-sizing: border-box;
}

.field-input:focus { border-color: rgba(10,132,255,0.5); }

.field-input:disabled { opacity: 0.5; cursor: not-allowed; }

.password-wrapper { position: relative; }

.password-wrapper .field-input { padding-right: 44px; }

.pw-toggle {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  display: flex;
  align-items: center;
}

.review-table {
  background: rgba(255,255,255,0.03);
  border: 1px solid rgba(255,255,255,0.07);
  border-radius: 12px;
  overflow: hidden;
}

.review-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid rgba(255,255,255,0.05);
}

.review-row:last-child { border-bottom: none; }

.review-label { font-size: 13px; color: rgba(255,255,255,0.4); }

.review-value { font-size: 14px; color: #F5F5F7; font-weight: 500; }

.confirm-actions { display: flex; gap: 10px; align-items: center; }

.reg-footer {
  font-size: 12px;
  color: rgba(255,255,255,0.2);
  margin-top: 24px;
}
</style>
