<template>
  <div class="list-page">
    <!-- Header -->
    <div class="list-header">
      <div>
        <h1 class="page-title">Rubrics</h1>
        <p class="page-sub">Manage peer evaluation rubrics</p>
      </div>
      <v-btn color="primary" prepend-icon="mdi-plus" to="/rubrics/create" rounded="xl">
        New Rubric
      </v-btn>
    </div>

    <v-alert v-if="error" type="error" variant="tonal" class="mb-5" rounded="xl">{{ error }}</v-alert>

    <!-- Search -->
    <div class="search-row">
      <div class="search-wrap">
        <v-icon size="16" class="search-icon">mdi-magnify</v-icon>
        <input v-model="search" class="search-input" placeholder="Search rubrics…" />
      </div>
    </div>

    <!-- Loading -->
    <div v-if="rubricStore.loading" class="list-loading">
      <v-progress-circular indeterminate color="primary" size="28" />
    </div>

    <!-- Empty -->
    <div v-else-if="filtered.length === 0" class="list-empty">
      <v-icon size="40" style="opacity:0.15">mdi-book-off-outline</v-icon>
      <span>No rubrics found</span>
    </div>

    <!-- Cards -->
    <div v-else class="rubric-list">
      <button
        v-for="rubric in filtered"
        :key="rubric.id"
        class="rubric-row"
        @click="openSheet(rubric)"
      >
        <div class="rubric-icon">
          <v-icon size="18" color="#30D158">mdi-book-open-outline</v-icon>
        </div>
        <div class="rubric-info">
          <div class="rubric-name">{{ rubric.name }}</div>
          <div class="rubric-meta">{{ rubric.criteria?.length ?? 0 }} criteria</div>
        </div>
        <div class="rubric-actions" @click.stop>
          <v-btn icon="mdi-pencil" size="small" variant="text" density="comfortable"
                 :to="`/rubrics/${rubric.id}`" />
          <v-btn icon="mdi-delete-outline" size="small" variant="text" density="comfortable"
                 color="error" @click="confirmDelete(rubric)" />
        </div>
        <v-icon size="16" class="rubric-chevron">mdi-chevron-right</v-icon>
      </button>
    </div>

    <!-- Side sheet -->
    <v-navigation-drawer
      v-model="sheetOpen"
      location="end"
      :width="400"
      temporary
      style="background:#161618; border-left:1px solid rgba(255,255,255,0.07)"
    >
      <div v-if="selected" class="sheet-inner">
        <div class="sheet-header">
          <div>
            <div class="sheet-title">{{ selected.name }}</div>
            <div class="sheet-sub">{{ selected.criteria?.length ?? 0 }} criteria</div>
          </div>
          <button class="sheet-close" @click="sheetOpen = false">
            <v-icon size="18">mdi-close</v-icon>
          </button>
        </div>

        <div class="sheet-section-label">Criteria</div>
        <div v-if="selected.criteria?.length" class="sheet-criteria">
          <div v-for="c in selected.criteria" :key="c.id" class="criteria-row">
            <div class="criteria-name">{{ c.name }}</div>
            <div class="criteria-weight">{{ c.weight }}%</div>
          </div>
        </div>
        <div v-else class="sheet-empty">No criteria defined</div>

        <div class="sheet-actions">
          <v-btn color="primary" rounded="xl" block :to="`/rubrics/${selected.id}`">
            Edit Rubric
          </v-btn>
          <v-btn color="error" variant="tonal" rounded="xl" block @click="confirmDelete(selected)">
            Delete
          </v-btn>
        </div>
      </div>
    </v-navigation-drawer>

    <!-- Delete confirm dialog -->
    <v-dialog v-model="deleteDialog" max-width="420">
      <v-card elevation="0">
        <v-card-title class="pt-6 px-6">Delete Rubric</v-card-title>
        <v-card-text class="px-6">
          Delete <strong>{{ toDelete?.name }}</strong>? This cannot be undone.
        </v-card-text>
        <v-card-actions class="px-6 pb-6 ga-2">
          <v-spacer />
          <v-btn rounded="xl" variant="tonal" @click="deleteDialog = false">Cancel</v-btn>
          <v-btn rounded="xl" color="error" variant="flat" @click="doDelete">Delete</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRubricStore } from '@/stores/rubricStore'

const rubricStore = useRubricStore()
const error = ref('')
const search = ref('')
const sheetOpen = ref(false)
const selected = ref<any>(null)
const deleteDialog = ref(false)
const toDelete = ref<any>(null)

const filtered = computed(() => {
  const q = search.value.toLowerCase()
  if (!q) return rubricStore.rubrics
  return rubricStore.rubrics.filter((r: any) => r.name?.toLowerCase().includes(q))
})

onMounted(async () => {
  try { await rubricStore.fetchRubrics() }
  catch (e: any) { error.value = e?.response?.data?.message ?? 'Failed to load rubrics' }
})

function openSheet(rubric: any) {
  selected.value = rubric
  sheetOpen.value = true
}

function confirmDelete(item: any) {
  toDelete.value = item
  deleteDialog.value = true
}

async function doDelete() {
  try { await rubricStore.deleteRubric(toDelete.value.id) }
  catch (e: any) { error.value = e?.response?.data?.message ?? 'Delete failed' }
  finally {
    deleteDialog.value = false
    sheetOpen.value = false
    toDelete.value = null
  }
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

/* Search */
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

/* States */
.list-loading, .list-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 60px 0;
  color: rgba(255,255,255,0.25);
  font-size: 14px;
}

/* Rubric rows */
.rubric-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.rubric-row {
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
.rubric-row:hover {
  background: #1C1C1F;
  border-color: rgba(255,255,255,0.12);
}
.rubric-row:hover .rubric-chevron { opacity: 0.5; }

.rubric-icon {
  width: 36px;
  height: 36px;
  background: rgba(48, 209, 88, 0.12);
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.rubric-info { flex: 1; min-width: 0; }
.rubric-name {
  font-size: 14px;
  font-weight: 500;
  color: #F5F5F7;
}
.rubric-meta {
  font-size: 12px;
  color: rgba(255,255,255,0.35);
  margin-top: 1px;
}
.rubric-actions { display: flex; gap: 2px; }
.rubric-chevron {
  color: rgba(255,255,255,0.15);
  opacity: 0;
  transition: opacity 0.15s ease;
}

/* Side sheet */
.sheet-inner { padding: 24px 20px; height: 100%; display: flex; flex-direction: column; }
.sheet-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 28px;
}
.sheet-title { font-size: 18px; font-weight: 700; color: #F5F5F7; letter-spacing: -0.3px; }
.sheet-sub { font-size: 13px; color: rgba(255,255,255,0.4); margin-top: 2px; }
.sheet-close {
  background: rgba(255,255,255,0.06);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 8px;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: rgba(255,255,255,0.5);
}

.sheet-section-label {
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: rgba(255,255,255,0.3);
  margin-bottom: 10px;
}
.sheet-criteria { display: flex; flex-direction: column; gap: 6px; }
.criteria-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 14px;
  background: rgba(255,255,255,0.04);
  border-radius: 10px;
}
.criteria-name { font-size: 13px; color: rgba(255,255,255,0.8); }
.criteria-weight {
  font-size: 12px;
  font-weight: 600;
  color: #30D158;
  background: rgba(48,209,88,0.1);
  padding: 2px 8px;
  border-radius: 20px;
}
.sheet-empty {
  font-size: 13px;
  color: rgba(255,255,255,0.25);
  padding: 20px 0;
}
.sheet-actions {
  margin-top: auto;
  padding-top: 24px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
</style>
