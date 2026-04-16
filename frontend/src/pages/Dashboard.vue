<template>
  <div class="dash-page">
    <!-- Header -->
    <div class="dash-header">
      <div>
        <h1 class="page-title">Good {{ greeting }}, {{ firstName }}</h1>
        <p class="page-sub">Here's your workspace at a glance.</p>
      </div>
    </div>

    <!-- Bento grid -->
    <div class="bento-grid">
      <RouterLink
        v-for="card in cards"
        :key="card.title"
        :to="card.to"
        class="bento-cell"
        :class="card.span ?? ''"
        :style="`--accent: ${card.color}`"
      >
        <div class="bento-icon-row">
          <div class="bento-icon">
            <v-icon size="20" :color="card.color">{{ card.icon }}</v-icon>
          </div>
          <v-icon size="16" class="bento-arrow">mdi-arrow-top-right</v-icon>
        </div>
        <div class="bento-title">{{ card.title }}</div>
        <div class="bento-desc">{{ card.description }}</div>
      </RouterLink>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const authStore = useAuthStore()

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 12) return 'morning'
  if (h < 18) return 'afternoon'
  return 'evening'
})

const firstName = computed(() => {
  const name = authStore.username ?? ''
  return name.split('@')[0].split('.')[0]
    .replace(/^\w/, (c: string) => c.toUpperCase())
})

const adminCards = [
  { title: 'Rubrics',     description: 'Build and manage peer evaluation rubrics',  icon: 'mdi-book-open-outline',     color: '#30D158', to: '/rubrics',     span: 'span-2' },
  { title: 'Sections',    description: 'Manage course sections and weeks',          icon: 'mdi-layers-outline',        color: '#0A84FF', to: '/sections'    },
  { title: 'Teams',       description: 'Organise and view student project teams',   icon: 'mdi-account-group-outline', color: '#5E5CE6', to: '/teams'       },
  { title: 'Students',    description: 'View and manage enrolled students',         icon: 'mdi-school-outline',        color: '#64D2FF', to: '/students'    },
  { title: 'Instructors', description: 'Manage instructor accounts',               icon: 'mdi-account-tie-outline',   color: '#FF9F0A', to: '/instructors', span: 'span-2' },
]

const instructorCards = [
  { title: 'My Teams',       description: 'View your assigned teams',         icon: 'mdi-account-group-outline', color: '#0A84FF', to: '/teams',                     span: 'span-2' },
  { title: 'Students',       description: 'Browse your students',             icon: 'mdi-school-outline',        color: '#64D2FF', to: '/students'                   },
  { title: 'Section Report', description: 'Peer evaluation summary by team',  icon: 'mdi-chart-bar-stacked',     color: '#5E5CE6', to: '/evaluations/section-report' },
  { title: 'WAR Reports',    description: 'Weekly activity report overview',  icon: 'mdi-file-chart-outline',    color: '#30D158', to: '/activities/report',          span: 'span-2' },
]

const studentCards = [
  { title: 'My WAR',    description: 'Submit your weekly activity report',   icon: 'mdi-clipboard-list-outline', color: '#0A84FF', to: '/activities',            span: 'span-2' },
  { title: 'Peer Eval', description: 'Evaluate your teammates this week',    icon: 'mdi-star-outline',           color: '#FF9F0A', to: '/evaluations/submit'     },
  { title: 'My Report', description: 'See your aggregated evaluation scores', icon: 'mdi-chart-line-variant',    color: '#30D158', to: '/evaluations/my-report'  },
]

const cards = computed(() => {
  if (authStore.isAdmin) return adminCards
  if (authStore.isInstructor) return instructorCards
  return studentCards
})
</script>

<style scoped>
.dash-page {
  padding: 40px 32px 60px;
  max-width: 1100px;
  margin: 0 auto;
}

.dash-header {
  margin-bottom: 32px;
}

/* Bento grid */
.bento-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
}

@media (max-width: 900px) {
  .bento-grid { grid-template-columns: repeat(2, 1fr); }
  .span-2 { grid-column: span 1; }
}

@media (max-width: 560px) {
  .bento-grid { grid-template-columns: 1fr; }
  .span-2 { grid-column: span 1; }
}

.span-2 { grid-column: span 2; }

/* Cell */
.bento-cell {
  position: relative;
  display: flex;
  flex-direction: column;
  padding: 22px 22px 20px;
  background: #161618;
  border: 1px solid rgba(255, 255, 255, 0.07);
  border-radius: 18px;
  text-decoration: none;
  cursor: pointer;
  transition: background 0.18s ease, border-color 0.18s ease, transform 0.18s ease;
  min-height: 130px;
}
.bento-cell:hover {
  background: #1C1C1F;
  border-color: color-mix(in srgb, var(--accent) 35%, transparent);
  transform: translateY(-2px);
}
.bento-cell:hover .bento-arrow {
  opacity: 0.7;
  transform: translate(2px, -2px);
}

.bento-icon-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.bento-icon {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  background: color-mix(in srgb, var(--accent) 14%, transparent);
  display: flex;
  align-items: center;
  justify-content: center;
}

.bento-arrow {
  color: rgba(255, 255, 255, 0.2);
  opacity: 0;
  transition: opacity 0.15s ease, transform 0.15s ease;
}

.bento-title {
  font-size: 15px;
  font-weight: 600;
  color: #F5F5F7;
  letter-spacing: -0.2px;
  margin-bottom: 4px;
}

.bento-desc {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.38);
  line-height: 1.5;
}
</style>
