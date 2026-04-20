<template>
  <div class="list-page">
    <div class="list-header">
      <div>
        <h1 class="page-title">Teams</h1>
        <p class="page-sub">Find and manage senior design teams</p>
      </div>
      <v-btn v-if="authStore.isAdmin" color="primary" prepend-icon="mdi-plus" to="/teams/create" rounded="xl">
        New Team
      </v-btn>
    </div>

    <!-- Search -->
    <div class="search-row">
      <div class="search-wrap">
        <v-icon size="16" class="search-icon">mdi-magnify</v-icon>
        <input v-model="search" class="search-input" placeholder="Search teams…" @input="onSearch" />
      </div>
    </div>

    <!-- Loading -->
    <div v-if="teamStore.loading" class="list-loading">
      <v-progress-circular indeterminate color="primary" size="28" />
    </div>

    <!-- Empty -->
    <div v-else-if="teamStore.teams.length === 0" class="list-empty">
      <v-icon size="40" style="opacity:0.15">mdi-account-group-outline</v-icon>
      <span>No teams found</span>
    </div>

    <!-- Rows -->
    <div v-else class="item-list">
      <button
        v-for="team in teamStore.teams"
        :key="team.id"
        class="item-row"
        @click="$router.push(`/teams/${team.id}`)"
      >
        <div class="item-icon">
          <v-icon size="18" color="#30D158">mdi-account-group-outline</v-icon>
        </div>
        <div class="item-info">
          <div class="item-name">{{ team.name }}</div>
          <div class="item-meta">
            {{ team.students?.length ?? 0 }} student{{ team.students?.length !== 1 ? 's' : '' }}
            <span v-if="team.description" class="item-desc">· {{ team.description }}</span>
          </div>
        </div>
        <div class="item-actions" @click.stop>
          <v-btn v-if="authStore.isAdmin" icon="mdi-pencil" size="small" variant="text" density="comfortable"
                 :to="`/teams/${team.id}/edit`" />
        </div>
        <v-icon size="16" class="item-chevron">mdi-chevron-right</v-icon>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useTeamStore } from '@/stores/teamStore'
import { useAuthStore } from '@/stores/authStore'

const teamStore = useTeamStore()
const authStore = useAuthStore()
const search = ref('')

onMounted(() => teamStore.fetchTeams())

function onSearch() {
  teamStore.fetchTeams(search.value ? { name: search.value } : undefined)
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
.item-row { display: flex; align-items: center; gap: 14px; width: 100%; padding: 14px 16px; background: #161618; border: 1px solid rgba(255,255,255,0.07); border-radius: 14px; cursor: pointer; text-align: left; font-family: inherit; transition: background 0.15s ease, border-color 0.15s ease; }
.item-row:hover { background: #1C1C1F; border-color: rgba(255,255,255,0.12); }
.item-icon { width: 36px; height: 36px; background: rgba(48,209,88,0.12); border-radius: 9px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.item-info { flex: 1; min-width: 0; }
.item-name { font-size: 14px; font-weight: 500; color: #F5F5F7; }
.item-meta { font-size: 12px; color: rgba(255,255,255,0.35); margin-top: 2px; }
.item-desc { opacity: 0.7; }
.item-actions { display: flex; gap: 2px; }
.item-chevron { color: rgba(255,255,255,0.15); opacity: 0; transition: opacity 0.15s ease; }
.item-row:hover .item-chevron { opacity: 0.5; }
</style>
