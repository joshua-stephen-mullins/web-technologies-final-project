<template>
  <div class="list-page">
    <div class="list-header">
      <div>
        <h1 class="page-title">Sections</h1>
        <p class="page-sub">Manage course sections and weeks</p>
      </div>
      <v-btn color="primary" prepend-icon="mdi-plus" to="/sections/create" rounded="xl">
        New Section
      </v-btn>
    </div>

    <!-- Search -->
    <div class="search-row">
      <div class="search-wrap">
        <v-icon size="16" class="search-icon">mdi-magnify</v-icon>
        <input v-model="search" class="search-input" placeholder="Search sections…" @input="onSearch" />
      </div>
    </div>

    <!-- Loading -->
    <div v-if="sectionStore.loading" class="list-loading">
      <v-progress-circular indeterminate color="primary" size="28" />
    </div>

    <!-- Empty -->
    <div v-else-if="sectionStore.sections.length === 0" class="list-empty">
      <v-icon size="40" style="opacity:0.15">mdi-layers-off-outline</v-icon>
      <span>No sections found</span>
    </div>

    <!-- Rows -->
    <div v-else class="section-list">
      <button
        v-for="section in sectionStore.sections"
        :key="section.id"
        class="section-row"
        @click="$router.push(`/sections/${section.id}`)"
      >
        <div class="section-icon">
          <v-icon size="18" color="#0A84FF">mdi-layers-outline</v-icon>
        </div>
        <div class="section-info">
          <div class="section-name">{{ section.name }}</div>
          <div class="section-meta">
            {{ section.startDate }} – {{ section.endDate }}
            <span v-if="section.rubricName" class="section-tag">{{ section.rubricName }}</span>
          </div>
        </div>
        <div class="section-actions" @click.stop>
          <v-btn icon="mdi-pencil" size="small" variant="text" density="comfortable"
                 :to="`/sections/${section.id}/edit`" />
          <v-btn icon="mdi-calendar-week" size="small" variant="text" density="comfortable"
                 :to="`/sections/${section.id}/weeks`" />
        </div>
        <v-icon size="16" class="section-chevron">mdi-chevron-right</v-icon>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useSectionStore } from '@/stores/sectionStore'

const sectionStore = useSectionStore()
const search = ref('')

onMounted(() => sectionStore.fetchSections())

function onSearch() {
  sectionStore.fetchSections(search.value || undefined)
}
</script>

<style scoped>
.list-page {
  padding: 40px 32px 60px;
  max-width: 860px;
  margin: 0 auto;
}
.list-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 28px;
}

.search-row { margin-bottom: 16px; }
.search-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 320px;
  background: #161618;
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 10px;
  padding: 8px 12px;
}
.search-icon { color: rgba(255,255,255,0.3); flex-shrink: 0; }
.search-input {
  flex: 1;
  background: none;
  border: none;
  outline: none;
  font-family: inherit;
  font-size: 14px;
  color: #F5F5F7;
}
.search-input::placeholder { color: rgba(255,255,255,0.25); }

.list-loading, .list-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 60px 0;
  color: rgba(255,255,255,0.25);
  font-size: 14px;
}

.section-list { display: flex; flex-direction: column; gap: 6px; }

.section-row {
  display: flex;
  align-items: center;
  gap: 14px;
  width: 100%;
  padding: 14px 16px;
  background: #161618;
  border: 1px solid rgba(255,255,255,0.07);
  border-radius: 14px;
  cursor: pointer;
  text-align: left;
  font-family: inherit;
  transition: background 0.15s ease, border-color 0.15s ease;
}
.section-row:hover {
  background: #1C1C1F;
  border-color: rgba(255,255,255,0.12);
}
.section-row:hover .section-chevron { opacity: 0.5; }

.section-icon {
  width: 36px;
  height: 36px;
  background: rgba(10, 132, 255, 0.12);
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.section-info { flex: 1; min-width: 0; }
.section-name { font-size: 14px; font-weight: 500; color: #F5F5F7; }
.section-meta {
  font-size: 12px;
  color: rgba(255,255,255,0.35);
  margin-top: 2px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.section-tag {
  background: rgba(10,132,255,0.12);
  color: #0A84FF;
  font-size: 11px;
  font-weight: 500;
  padding: 1px 7px;
  border-radius: 20px;
}
.section-actions { display: flex; gap: 2px; }
.section-chevron {
  color: rgba(255,255,255,0.15);
  opacity: 0;
  transition: opacity 0.15s ease;
}
</style>
