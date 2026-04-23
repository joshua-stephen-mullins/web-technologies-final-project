<template>
  <div class="list-page">
    <div class="list-header">
      <div>
        <h1 class="page-title">Students</h1>
        <p class="page-sub">Find and manage students</p>
      </div>
      <v-btn v-if="authStore.isAdmin" color="primary" prepend-icon="mdi-email-plus-outline" to="/students/invite" rounded="xl">
        Invite Students
      </v-btn>
    </div>

    <!-- Search -->
    <div class="search-row">
      <div class="search-wrap">
        <v-icon size="16" class="search-icon">mdi-magnify</v-icon>
        <input v-model="search" class="search-input" placeholder="Search by name or email…" @input="onSearch" />
      </div>
    </div>

    <div v-if="studentStore.loading" class="list-loading">
      <v-progress-circular indeterminate color="primary" size="28" />
    </div>

    <div v-else-if="studentStore.students.length === 0" class="list-empty">
      <v-icon size="40" style="opacity:0.15">mdi-account-off-outline</v-icon>
      <span>No students found</span>
    </div>

    <div v-else class="item-list">
      <button
        v-for="student in studentStore.students"
        :key="student.id"
        class="item-row"
        @click="$router.push(`/students/${student.id}`)"
      >
        <div class="item-icon">
          <v-icon size="18" color="#0A84FF">mdi-account-outline</v-icon>
        </div>
        <div class="item-info">
          <div class="item-name">{{ student.firstName }} {{ student.lastName }}</div>
          <div class="item-meta">
            {{ student.email }}
            <span v-if="student.teamName" class="item-tag">{{ student.teamName }}</span>
          </div>
        </div>
        <v-icon size="16" class="item-chevron">mdi-chevron-right</v-icon>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useStudentStore } from '@/stores/studentStore'
import { useAuthStore } from '@/stores/authStore'

const studentStore = useStudentStore()
const authStore = useAuthStore()
const search = ref('')

onMounted(() => studentStore.fetchStudents())

function onSearch() {
  const q = search.value.trim()
  if (!q) {
    studentStore.fetchStudents()
  } else if (q.includes('@')) {
    studentStore.fetchStudents({ email: q })
  } else {
    const parts = q.split(' ')
    studentStore.fetchStudents(parts.length > 1
      ? { firstName: parts[0], lastName: parts[1] }
      : { firstName: q })
  }
}
</script>

<style scoped>
.list-page { padding: 40px 32px 60px; max-width: 860px; margin: 0 auto; }
.list-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 28px; }
.page-title { font-size: 22px; font-weight: 600; color: #F5F5F7; margin: 0; }
.page-sub { font-size: 13px; color: rgba(255,255,255,0.4); margin: 4px 0 0; }
.search-row { margin-bottom: 16px; }
.search-wrap { display: flex; align-items: center; gap: 8px; width: 320px; background: #161618; border: 1px solid rgba(255,255,255,0.08); border-radius: 10px; padding: 8px 12px; }
.search-icon { color: rgba(255,255,255,0.3); flex-shrink: 0; }
.search-input { flex: 1; background: none; border: none; outline: none; font-family: inherit; font-size: 14px; color: #F5F5F7; }
.search-input::placeholder { color: rgba(255,255,255,0.25); }
.list-loading, .list-empty { display: flex; flex-direction: column; align-items: center; gap: 12px; padding: 60px 0; color: rgba(255,255,255,0.25); font-size: 14px; }
.item-list { display: flex; flex-direction: column; gap: 6px; }
.item-row { display: flex; align-items: center; gap: 14px; width: 100%; padding: 14px 16px; background: #161618; border: 1px solid rgba(255,255,255,0.07); border-radius: 14px; cursor: pointer; text-align: left; font-family: inherit; transition: background 0.15s ease; }
.item-row:hover { background: #1C1C1F; border-color: rgba(255,255,255,0.12); }
.item-icon { width: 36px; height: 36px; background: rgba(10,132,255,0.12); border-radius: 9px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.item-info { flex: 1; min-width: 0; }
.item-name { font-size: 14px; font-weight: 500; color: #F5F5F7; }
.item-meta { font-size: 12px; color: rgba(255,255,255,0.35); margin-top: 2px; display: flex; align-items: center; gap: 8px; }
.item-tag { background: rgba(10,132,255,0.12); color: #0A84FF; font-size: 11px; font-weight: 500; padding: 1px 7px; border-radius: 20px; }
.item-chevron { color: rgba(255,255,255,0.15); opacity: 0; transition: opacity 0.15s ease; margin-left: auto; }
.item-row:hover .item-chevron { opacity: 0.5; }
</style>
