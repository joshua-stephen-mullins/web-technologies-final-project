<template>
  <Teleport to="body">
    <Transition name="palette">
      <div v-if="modelValue" class="palette-backdrop" @mousedown.self="close">
        <div class="palette-box">
          <!-- Search input -->
          <div class="palette-search">
            <v-icon size="18" style="color:rgba(255,255,255,0.35); flex-shrink:0">mdi-magnify</v-icon>
            <input
              ref="inputRef"
              v-model="query"
              class="palette-input"
              placeholder="Search pages and actions…"
              @keydown.escape="close"
              @keydown.arrow-down.prevent="moveDown"
              @keydown.arrow-up.prevent="moveUp"
              @keydown.enter.prevent="selectActive"
            />
            <kbd class="esc-key">esc</kbd>
          </div>

          <!-- Results -->
          <div v-if="grouped.length" class="palette-results">
            <template v-for="grp in grouped" :key="grp.group">
              <div class="palette-group-label">{{ grp.group }}</div>
              <button
                v-for="(item, idx) in grp.items"
                :key="item.to"
                class="palette-item"
                :class="{ active: flatActive === flatIndex(grp, idx) }"
                @mouseenter="flatActive = flatIndex(grp, idx)"
                @click="navigate(item.to)"
              >
                <div class="item-icon-wrap">
                  <v-icon size="16">{{ item.icon }}</v-icon>
                </div>
                <span class="item-title">{{ item.title }}</span>
                <span class="item-group-tag">{{ grp.group }}</span>
              </button>
            </template>
          </div>
          <div v-else class="palette-empty">
            <v-icon size="32" style="opacity:0.2">mdi-compass-off-outline</v-icon>
            <span>No results for "{{ query }}"</span>
          </div>

          <!-- Footer hint -->
          <div class="palette-footer">
            <span><kbd>↑↓</kbd> navigate</span>
            <span><kbd>↵</kbd> open</span>
            <span><kbd>esc</kbd> close</span>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const props = defineProps<{ modelValue: boolean }>()
const emit  = defineEmits<{ (e: 'update:modelValue', v: boolean): void }>()

const router   = useRouter()
const auth     = useAuthStore()
const query    = ref('')
const flatActive = ref(0)
const inputRef = ref<HTMLInputElement | null>(null)

watch(() => props.modelValue, (val) => {
  if (val) {
    query.value = ''
    flatActive.value = 0
    nextTick(() => inputRef.value?.focus())
  }
})

interface Command { title: string; icon: string; to: string; keywords?: string }
interface Group   { group: string; items: Command[] }

const allCommands = computed<Command[]>(() => {
  const cmds: Command[] = [
    { title: 'Dashboard',       icon: 'mdi-home-outline',           to: '/',        keywords: 'home overview' },
    { title: 'Profile',         icon: 'mdi-account-outline',        to: '/profile', keywords: 'account settings' },
  ]
  if (auth.isAdmin || auth.isInstructor) {
    cmds.push(
      { title: 'Rubrics',         icon: 'mdi-book-open-outline',       to: '/rubrics',     keywords: 'criteria evaluation' },
      { title: 'New Rubric',      icon: 'mdi-plus-circle-outline',     to: '/rubrics/create', keywords: 'create add' },
      { title: 'Sections',        icon: 'mdi-layers-outline',          to: '/sections',    keywords: 'course semester' },
      { title: 'New Section',     icon: 'mdi-plus-circle-outline',     to: '/sections/create', keywords: 'create add' },
      { title: 'Teams',           icon: 'mdi-account-group-outline',   to: '/teams',       keywords: 'group project' },
      { title: 'Students',        icon: 'mdi-school-outline',          to: '/students',    keywords: 'people users' },
      { title: 'Section Report',  icon: 'mdi-chart-bar-stacked',       to: '/evaluations/section-report', keywords: 'grades report' },
      { title: 'WAR Reports',     icon: 'mdi-file-chart-outline',      to: '/activities/report', keywords: 'weekly activity' },
    )
  }
  if (auth.isAdmin) {
    cmds.push(
      { title: 'Instructors',    icon: 'mdi-account-tie-outline',     to: '/instructors', keywords: 'teacher faculty' },
    )
  }
  if (auth.isStudent) {
    cmds.push(
      { title: 'My WAR',         icon: 'mdi-clipboard-list-outline',  to: '/activities',             keywords: 'weekly activity report' },
      { title: 'Peer Evaluation',icon: 'mdi-star-outline',            to: '/evaluations/submit',     keywords: 'evaluate teammate' },
      { title: 'My Eval Report', icon: 'mdi-chart-line-variant',      to: '/evaluations/my-report',  keywords: 'grades scores' },
    )
  }
  return cmds
})

const filtered = computed(() => {
  const q = query.value.toLowerCase()
  if (!q) return allCommands.value
  return allCommands.value.filter(c =>
    c.title.toLowerCase().includes(q) || (c.keywords ?? '').includes(q)
  )
})

const grouped = computed<Group[]>(() => {
  const groupMap: Record<string, Command[]> = {}
  for (const cmd of filtered.value) {
    const grp = cmd.to.startsWith('/rubrics/create') || cmd.to.startsWith('/sections/create')
      ? 'Create' : 'Navigate'
    if (!groupMap[grp]) groupMap[grp] = []
    groupMap[grp].push(cmd)
  }
  return Object.entries(groupMap).map(([group, items]) => ({ group, items }))
})

const flatList = computed(() => grouped.value.flatMap(g => g.items))

function flatIndex(grp: Group, itemIdx: number) {
  let offset = 0
  for (const g of grouped.value) {
    if (g.group === grp.group) return offset + itemIdx
    offset += g.items.length
  }
  return 0
}

function moveDown() {
  flatActive.value = Math.min(flatActive.value + 1, flatList.value.length - 1)
}
function moveUp() {
  flatActive.value = Math.max(flatActive.value - 1, 0)
}
function selectActive() {
  const item = flatList.value[flatActive.value]
  if (item) navigate(item.to)
}
function navigate(to: string) {
  router.push(to)
  close()
}
function close() {
  emit('update:modelValue', false)
}

watch(query, () => { flatActive.value = 0 })
</script>

<style scoped>
.palette-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.65);
  backdrop-filter: blur(8px);
  z-index: 9999;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 16vh;
}
.palette-box {
  width: 100%;
  max-width: 580px;
  background: #1A1A1D;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 32px 80px rgba(0, 0, 0, 0.6);
  margin: 0 16px;
}
.palette-search {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.07);
}
.palette-input {
  flex: 1;
  background: none;
  border: none;
  outline: none;
  font-family: inherit;
  font-size: 16px;
  color: #F5F5F7;
  caret-color: #0A84FF;
}
.palette-input::placeholder { color: rgba(255,255,255,0.25); }
.esc-key {
  font-family: inherit;
  font-size: 11px;
  padding: 2px 6px;
  background: rgba(255,255,255,0.06);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 5px;
  color: rgba(255,255,255,0.3);
}
.palette-results {
  max-height: 360px;
  overflow-y: auto;
  padding: 6px;
}
.palette-group-label {
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.07em;
  text-transform: uppercase;
  color: rgba(255,255,255,0.3);
  padding: 8px 10px 4px;
}
.palette-item {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 9px 10px;
  background: none;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  text-align: left;
  font-family: inherit;
  transition: background 0.1s ease;
}
.palette-item.active {
  background: rgba(10, 132, 255, 0.15);
}
.palette-item.active .item-title {
  color: #FFFFFF;
}
.item-icon-wrap {
  width: 30px;
  height: 30px;
  background: rgba(255,255,255,0.06);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: rgba(255,255,255,0.6);
}
.item-title {
  flex: 1;
  font-size: 14px;
  font-weight: 500;
  color: rgba(255,255,255,0.75);
}
.item-group-tag {
  font-size: 11px;
  color: rgba(255,255,255,0.2);
}
.palette-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 40px 0;
  color: rgba(255,255,255,0.25);
  font-size: 14px;
}
.palette-footer {
  display: flex;
  gap: 16px;
  padding: 8px 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  font-size: 11px;
  color: rgba(255,255,255,0.25);
}
.palette-footer kbd {
  font-family: inherit;
  padding: 1px 5px;
  background: rgba(255,255,255,0.07);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 4px;
  margin-right: 4px;
}

/* Transition */
.palette-enter-active, .palette-leave-active { transition: opacity 0.15s ease; }
.palette-enter-from, .palette-leave-to { opacity: 0; }
.palette-enter-active .palette-box,
.palette-leave-active .palette-box { transition: transform 0.15s ease; }
.palette-enter-from .palette-box  { transform: scale(0.97) translateY(-8px); }
.palette-leave-to .palette-box    { transform: scale(0.97) translateY(-8px); }
</style>
