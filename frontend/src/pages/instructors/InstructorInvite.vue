<template>
  <div class="invite-page">
    <div class="invite-header">
      <v-btn icon="mdi-arrow-left" variant="text" to="/instructors" />
      <div>
        <h1 class="page-title">Invite Instructors</h1>
        <p class="page-sub">Send account setup emails to one or more instructors</p>
      </div>
    </div>

    <div class="invite-card">
      <label class="field-label">Email Addresses</label>
      <p class="field-hint">Enter one or more emails separated by semicolons ( ; )</p>
      <textarea
        v-model="rawInput"
        class="email-textarea"
        placeholder="jane@tcu.edu; john@tcu.edu; ..."
        rows="5"
        @input="parseEmails"
      />

      <!-- Preview -->
      <div v-if="parsedEmails.length > 0" class="preview-section">
        <div class="preview-label">Will invite {{ parsedEmails.length }} address(es):</div>
        <div class="email-chips">
          <span v-for="email in parsedEmails" :key="email" class="email-chip">{{ email }}</span>
        </div>
      </div>

      <div v-if="error" class="error-msg">{{ error }}</div>
      <div v-if="successMsg" class="success-msg">{{ successMsg }}</div>

      <div class="actions">
        <v-btn
          color="primary"
          rounded="xl"
          :disabled="parsedEmails.length === 0 || loading"
          :loading="loading"
          @click="sendInvites"
        >
          Send Invitations
        </v-btn>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useInstructorStore } from '@/stores/instructorStore'

const instructorStore = useInstructorStore()

const rawInput = ref('')
const parsedEmails = ref<string[]>([])
const loading = ref(false)
const error = ref('')
const successMsg = ref('')

function parseEmails() {
  error.value = ''
  successMsg.value = ''
  parsedEmails.value = rawInput.value
    .split(';')
    .map(e => e.trim())
    .filter(e => e.length > 0)
}

async function sendInvites() {
  error.value = ''
  successMsg.value = ''
  loading.value = true
  try {
    await instructorStore.inviteInstructors(parsedEmails.value)
    successMsg.value = `${parsedEmails.value.length} invitation(s) sent successfully.`
    rawInput.value = ''
    parsedEmails.value = []
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? 'Failed to send invitations.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.invite-page {
  padding: 40px 32px 60px;
  max-width: 640px;
  margin: 0 auto;
}
.invite-header {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 32px;
}
.page-title {
  font-size: 22px;
  font-weight: 600;
  color: #F5F5F7;
  margin: 0;
}
.page-sub {
  font-size: 13px;
  color: rgba(255,255,255,0.4);
  margin: 4px 0 0;
}

.invite-card {
  background: #161618;
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 16px;
  padding: 28px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.field-label {
  font-size: 13px;
  font-weight: 500;
  color: #F5F5F7;
}
.field-hint {
  font-size: 12px;
  color: rgba(255,255,255,0.35);
  margin: 0;
}

.email-textarea {
  width: 100%;
  background: #0D0D0F;
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 10px;
  padding: 12px;
  font-family: inherit;
  font-size: 14px;
  color: #F5F5F7;
  resize: vertical;
  outline: none;
  box-sizing: border-box;
}
.email-textarea:focus {
  border-color: rgba(10,132,255,0.5);
}
.email-textarea::placeholder { color: rgba(255,255,255,0.2); }

.preview-section { display: flex; flex-direction: column; gap: 8px; }
.preview-label { font-size: 12px; color: rgba(255,255,255,0.4); }
.email-chips { display: flex; flex-wrap: wrap; gap: 6px; }
.email-chip {
  background: rgba(10,132,255,0.12);
  color: #0A84FF;
  font-size: 12px;
  padding: 3px 10px;
  border-radius: 20px;
}

.error-msg {
  font-size: 13px;
  color: #FF453A;
  padding: 10px 14px;
  background: rgba(255,69,58,0.08);
  border-radius: 8px;
}
.success-msg {
  font-size: 13px;
  color: #30D158;
  padding: 10px 14px;
  background: rgba(48,209,88,0.08);
  border-radius: 8px;
}

.actions { display: flex; justify-content: flex-end; margin-top: 4px; }
</style>
