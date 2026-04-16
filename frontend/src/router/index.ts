import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

// Shared - Owner: Josh sets up base routes; each person adds their own routes here
const router = createRouter({
  history: createWebHistory(),
  routes: [
    // --- Auth (Josh) ---
    { path: '/login', component: () => import('@/pages/Login.vue') },
    { path: '/register', component: () => import('@/pages/Register.vue') },
    { path: '/forgot-password', component: () => import('@/pages/ForgotPassword.vue') },
    { path: '/reset-password', component: () => import('@/pages/ResetPassword.vue') },

    // --- Authenticated layout ---
    {
      path: '/',
      meta: { requiresAuth: true },
      children: [
        { path: '', component: () => import('@/pages/Dashboard.vue') },

        // --- Josh: Rubrics ---
        { path: 'rubrics', component: () => import('@/pages/rubrics/RubricList.vue') },
        { path: 'rubrics/create', component: () => import('@/pages/rubrics/RubricCreate.vue') },
        { path: 'rubrics/:id', component: () => import('@/pages/rubrics/RubricDetail.vue') },

        // --- Josh: Sections ---
        { path: 'sections', component: () => import('@/pages/sections/SectionList.vue') },
        { path: 'sections/create', component: () => import('@/pages/sections/SectionCreate.vue') },
        { path: 'sections/:id', component: () => import('@/pages/sections/SectionDetail.vue') },
        { path: 'sections/:id/edit', component: () => import('@/pages/sections/SectionEdit.vue') },
        { path: 'sections/:id/weeks', component: () => import('@/pages/sections/SectionWeeks.vue') },

        // --- Josh: Profile ---
        { path: 'profile', component: () => import('@/pages/currentUser/Profile.vue') },

        // --- Oscar: Teams ---
        { path: 'teams', component: () => import('@/pages/teams/TeamList.vue') },
        { path: 'teams/create', component: () => import('@/pages/teams/TeamCreate.vue') },
        { path: 'teams/:id', component: () => import('@/pages/teams/TeamDetail.vue') },
        { path: 'teams/:id/edit', component: () => import('@/pages/teams/TeamEdit.vue') },

        // --- Oscar: Students ---
        { path: 'students', component: () => import('@/pages/students/StudentList.vue') },
        { path: 'students/invite', component: () => import('@/pages/students/StudentInvite.vue') },
        { path: 'students/:id', component: () => import('@/pages/students/StudentDetail.vue') },

        // --- Oscar: Activities / WAR ---
        { path: 'activities', component: () => import('@/pages/activities/WeeklyActivityReport.vue') },
        { path: 'activities/report', component: () => import('@/pages/activities/WARReport.vue') },

        // --- Whitney: Instructors ---
        { path: 'instructors', component: () => import('@/pages/instructors/InstructorList.vue') },
        { path: 'instructors/invite', component: () => import('@/pages/instructors/InstructorInvite.vue') },
        { path: 'instructors/:id', component: () => import('@/pages/instructors/InstructorDetail.vue') },

        // --- Whitney: Evaluations ---
        { path: 'evaluations/submit', component: () => import('@/pages/evaluations/PeerEvaluationSubmit.vue') },
        { path: 'evaluations/my-report', component: () => import('@/pages/evaluations/MyEvaluationReport.vue') },
        { path: 'evaluations/section-report', component: () => import('@/pages/evaluations/SectionEvaluationReport.vue') },
        { path: 'evaluations/student-report/:id', component: () => import('@/pages/evaluations/StudentEvaluationReport.vue') },
        { path: 'evaluations/war-report/:id', component: () => import('@/pages/evaluations/StudentWARReport.vue') },
      ],
    },

    // --- Errors ---
    { path: '/403', component: () => import('@/pages/errors/Forbidden.vue') },
    { path: '/:pathMatch(.*)*', component: () => import('@/pages/errors/NotFound.vue') },
  ],
})

// Route guard: redirect to login if not authenticated
router.beforeEach((to) => {
  const authStore = useAuthStore()
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return '/login'
  }
})

export default router
