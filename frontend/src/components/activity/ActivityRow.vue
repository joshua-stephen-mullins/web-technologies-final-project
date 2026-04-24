<template>
  <tr class="activity-row">
    <td class="cell">
      <span class="category-chip" :class="categoryClass">{{ activity.category }}</span>
    </td>
    <td class="cell desc-cell">{{ activity.description }}</td>
    <td class="cell num-cell">{{ activity.plannedHours }}</td>
    <td class="cell num-cell">{{ activity.actualHours ?? '—' }}</td>
    <td class="cell">
      <span class="status-chip" :class="statusClass">{{ formatStatus(activity.status) }}</span>
    </td>
    <td class="cell actions-cell">
      <v-btn icon="mdi-pencil" size="x-small" variant="text" @click="$emit('edit', activity)" />
      <v-btn icon="mdi-delete-outline" size="x-small" variant="text" color="error" @click="$emit('delete', activity.id)" />
    </td>
  </tr>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{ activity: any }>()
defineEmits<{ edit: [activity: any]; delete: [id: number] }>()

const categoryClass = computed(() => `cat-${props.activity.category?.toLowerCase()}`)
const statusClass = computed(() => `st-${props.activity.status?.toLowerCase().replace('_', '-')}`)

function formatStatus(s: string) {
  return s === 'IN_PROGRESS' ? 'In Progress'
    : s === 'UNDER_TESTING' ? 'Under Testing'
    : s === 'DONE' ? 'Done' : s
}
</script>

<style scoped>
.activity-row { border-bottom: 1px solid rgba(255,255,255,0.05); }
.activity-row:last-child { border-bottom: none; }
.cell { padding: 10px 12px; font-size: 13px; color: #F5F5F7; vertical-align: middle; }
.desc-cell { max-width: 280px; white-space: pre-wrap; word-break: break-word; }
.num-cell { text-align: right; width: 70px; }
.actions-cell { white-space: nowrap; text-align: right; width: 80px; }

.category-chip, .status-chip {
  display: inline-block;
  font-size: 11px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: 20px;
  white-space: nowrap;
}
.cat-development { background: rgba(10,132,255,0.15); color: #0A84FF; }
.cat-testing { background: rgba(255,214,10,0.15); color: #FFD60A; }
.cat-bugfix { background: rgba(255,69,58,0.15); color: #FF453A; }
.cat-communication { background: rgba(48,209,88,0.15); color: #30D158; }
.cat-documentation { background: rgba(100,210,255,0.15); color: #64D2FF; }
.cat-design { background: rgba(191,90,242,0.15); color: #BF5AF2; }
.category-chip:not([class*="cat-"]) { background: rgba(255,255,255,0.08); color: rgba(255,255,255,0.6); }

.st-done { background: rgba(48,209,88,0.15); color: #30D158; }
.st-in-progress { background: rgba(255,159,10,0.15); color: #FF9F0A; }
.st-under-testing { background: rgba(100,210,255,0.15); color: #64D2FF; }
</style>
