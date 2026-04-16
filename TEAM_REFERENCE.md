# Project Pulse — Team Reference

**TCU Web Technologies — Spring 2026 — Dr. Wei**

| Team Member | Person | Use Cases | Backend Package(s) | Frontend Pages |
|---|---|---|---|---|
| Josh Mullins | Person 1 | UC-1,2,3,4,5,6,25,26,30 + Auth infra | `security/`, `user/`, `rubric/`, `section/` | `Login`, `Register`, `ForgotPassword`, `ResetPassword`, `Dashboard`, `Profile`, `rubrics/*`, `sections/*` |
| Oscar | Person 2 | UC-7,8,9,10,11,12,13,14,15,16,17,27,32 | `team/`, `student/`, `activity/` | `teams/*`, `students/*`, `activities/*` |
| Whitney | Person 3 | UC-18,19,20,21,22,23,24,28,29,31,33,34 | `instructor/`, `evaluation/` | `instructors/*`, `evaluations/*` |

---

## Use Case Assignments

### Josh (Person 1) — Auth + Rubrics + Sections

| UC | Description | Backend | Frontend |
|---|---|---|---|
| Auth | Login, JWT, Spring Security config | `security/SecurityConfig.java`, `security/JwtProvider.java`, `security/AuthController.java` | `authStore.ts`, `axiosClient.ts` |
| UC-25 | Student sets up account (invite token) | `user/UserController.java`, `user/userinvitation/` | `Register.vue` |
| UC-26 | Student edits own account | `user/UserController.java` | `currentUser/Profile.vue` |
| UC-30 | Instructor sets up account (invite token) | `user/UserController.java` | `Register.vue` (same flow) |
| UC-1 | Admin creates a rubric | `rubric/RubricController.java`, `rubric/RubricService.java` | `rubrics/RubricCreate.vue`, `components/rubric/CriterionEditor.vue` |
| UC-2 | Admin finds sections | `section/SectionController.java`, `section/SectionSpecs.java` | `sections/SectionList.vue` |
| UC-3 | Admin views a section | `section/SectionController.java` | `sections/SectionDetail.vue` |
| UC-4 | Admin creates a section | `section/SectionController.java`, `section/SectionService.java` | `sections/SectionCreate.vue` |
| UC-5 | Admin edits a section | `section/SectionController.java` | `sections/SectionEdit.vue` |
| UC-6 | Admin sets up active weeks | `section/SectionController.java`, `section/SectionService.java` | `sections/SectionWeeks.vue` |

**Critical note for Josh:** Your auth infrastructure (`SecurityConfig`, `JwtProvider`, `UserService`) is what everyone else depends on to test their secured endpoints. Prioritize this first.

---

### Oscar (Person 2) — Teams + Students + WAR

| UC | Description | Backend | Frontend |
|---|---|---|---|
| UC-7 | Admin/Instructor finds teams | `team/TeamController.java`, `team/TeamSpecs.java` | `teams/TeamList.vue` |
| UC-8 | Admin/Instructor views a team | `team/TeamController.java` | `teams/TeamDetail.vue` |
| UC-9 | Admin creates a team | `team/TeamController.java`, `team/TeamService.java` | `teams/TeamCreate.vue` |
| UC-10 | Admin edits a team | `team/TeamController.java` | `teams/TeamEdit.vue` |
| UC-11 | Admin invites students (email) | `user/userinvitation/UserInvitationService.java` | `students/StudentInvite.vue` |
| UC-12 | Admin assigns students to teams | `team/TeamService.java` | `teams/TeamDetail.vue` (assign button) |
| UC-13 | Admin removes student from team | `team/TeamService.java` | `teams/TeamDetail.vue` (remove button) |
| UC-14 | Admin deletes a team | `team/TeamService.java` | `teams/TeamDetail.vue` (delete button) |
| UC-15 | Admin/Instructor finds students | `student/StudentController.java`, `student/StudentSpecs.java` | `students/StudentList.vue` |
| UC-16 | Admin/Instructor views a student | `student/StudentController.java` | `students/StudentDetail.vue` |
| UC-17 | Admin deletes a student | `student/StudentService.java` | `students/StudentDetail.vue` (delete button) |
| UC-27 | Student manages WAR activities | `activity/ActivityController.java`, `activity/ActivityService.java` | `activities/WeeklyActivityReport.vue`, `components/activity/ActivityRow.vue` |
| UC-32 | Instructor/Student generates team WAR report | `activity/ActivityController.java` | `activities/WARReport.vue` |

**Note for Oscar:** UC-11 reuses Josh's `UserInvitationService` — coordinate on the email format. UC-12/13 are implemented on the `TeamDetail` page, not separate pages.

---

### Whitney (Person 3) — Instructors + Peer Evaluations

| UC | Description | Backend | Frontend |
|---|---|---|---|
| UC-18 | Admin invites instructors (email) | `user/userinvitation/UserInvitationService.java` | `instructors/InstructorInvite.vue` |
| UC-19 | Admin assigns instructors to teams | `instructor/InstructorService.java` | `teams/TeamDetail.vue` (assign button — coordinate with Oscar) |
| UC-20 | Admin removes instructor from team | `instructor/InstructorService.java` | `teams/TeamDetail.vue` (remove button) |
| UC-21 | Admin finds instructors | `instructor/InstructorController.java`, `instructor/InstructorSpecs.java` | `instructors/InstructorList.vue` |
| UC-22 | Admin views an instructor | `instructor/InstructorController.java` | `instructors/InstructorDetail.vue` |
| UC-23 | Admin deactivates an instructor | `instructor/InstructorService.java` | `instructors/InstructorDetail.vue` (deactivate button) |
| UC-24 | Admin reactivates an instructor | `instructor/InstructorService.java` | `instructors/InstructorDetail.vue` (reactivate button) |
| UC-28 | Student submits peer evaluation | `evaluation/EvaluationController.java`, `evaluation/EvaluationService.java` | `evaluations/PeerEvaluationSubmit.vue`, `components/evaluation/EvaluationTable.vue` |
| UC-29 | Student views own eval report | `evaluation/EvaluationController.java` | `evaluations/MyEvaluationReport.vue` |
| UC-31 | Instructor generates section eval report | `evaluation/EvaluationService.java` | `evaluations/SectionEvaluationReport.vue` |
| UC-33 | Instructor generates student eval report | `evaluation/EvaluationService.java` | `evaluations/StudentEvaluationReport.vue` |
| UC-34 | Instructor generates student WAR report | `activity/ActivityService.java` (Oscar's) | `evaluations/StudentWARReport.vue` |

**Note for Whitney:** UC-19/20 (assign/remove instructor on a team) will need to be added to Oscar's `TeamDetail.vue` page — coordinate so you're not both editing the same file. UC-34 calls Oscar's activity backend — his API must be ready first.

---

## Cross-Cutting Dependencies

```
Josh's auth layer  →  everyone's secured endpoints depend on it
Oscar's TeamDetail →  Whitney needs to add UC-19/20 buttons to it
Oscar's ActivityService → Whitney's UC-34 calls it
Josh's UserInvitationService → Oscar (UC-11) and Whitney (UC-18) both call it
```

**Recommended build order:**
1. Josh completes auth + `PeerEvaluationUser` entity first — everyone needs this to run locally
2. Josh completes Flyway migrations V1–V3 — Oscar and Whitney need the schema
3. Oscar builds team/student domain — Whitney's instructor assignment needs teams to exist
4. Everyone can then build their feature pages in parallel

---

## Tech Stack Quick Reference

| Layer | Technology | Notes |
|---|---|---|
| Backend | Spring Boot 4.0.2, Java 21 | Maven build |
| Frontend | Vue 3 + TypeScript + Vuetify 3 | Vite dev server on port 5173 |
| Database | MySQL 8 | Flyway manages schema migrations |
| Auth | JWT via Spring Security OAuth2 Resource Server | Token stored in localStorage |
| Email (dev) | Mailpit | Web UI at http://localhost:8025 |
| Deployment | Microsoft Azure | |

---

## Running Locally

### Start the database and mail server
```bash
docker-compose up -d
```

### Start the backend
```bash
cd backend
./mvnw spring-boot:run
# runs on http://localhost:8080
```

### Start the frontend
```bash
cd frontend
npm install
npm run dev
# runs on http://localhost:5173
```

---

## API Endpoint Summary

| Method | URL | UC | Owner |
|---|---|---|---|
| POST | `/api/auth/login` | UC-25/30 | Josh |
| POST | `/api/users/register` | UC-25/30 | Josh |
| GET/PUT | `/api/users/me` | UC-26 | Josh |
| POST | `/api/users/forgot-password` | — | Josh |
| POST | `/api/users/reset-password` | — | Josh |
| GET/POST | `/api/rubrics` | UC-1 | Josh |
| GET/PUT | `/api/rubrics/{id}` | UC-1 | Josh |
| GET/POST | `/api/sections` | UC-2, UC-4 | Josh |
| GET/PUT | `/api/sections/{id}` | UC-3, UC-5 | Josh |
| PUT | `/api/sections/{id}/weeks` | UC-6 | Josh |
| GET/POST | `/api/teams` | UC-7, UC-9 | Oscar |
| GET/PUT/DELETE | `/api/teams/{id}` | UC-8, UC-10, UC-14 | Oscar |
| POST | `/api/teams/{id}/students` | UC-12 | Oscar |
| DELETE | `/api/teams/{id}/students/{sid}` | UC-13 | Oscar |
| GET/DELETE | `/api/students` | UC-15, UC-17 | Oscar |
| GET | `/api/students/{id}` | UC-16 | Oscar |
| POST | `/api/students/invite` | UC-11 | Oscar |
| GET/POST/PUT/DELETE | `/api/activities` | UC-27 | Oscar |
| GET | `/api/activities/report/team` | UC-32 | Oscar |
| GET/DELETE | `/api/instructors` | UC-21 | Whitney |
| GET | `/api/instructors/{id}` | UC-22 | Whitney |
| PUT | `/api/instructors/{id}/deactivate` | UC-23 | Whitney |
| PUT | `/api/instructors/{id}/reactivate` | UC-24 | Whitney |
| POST | `/api/instructors/invite` | UC-18 | Whitney |
| POST | `/api/teams/{id}/instructors` | UC-19 | Whitney |
| DELETE | `/api/teams/{id}/instructors/{iid}` | UC-20 | Whitney |
| POST | `/api/evaluations` | UC-28 | Whitney |
| GET | `/api/evaluations/my-report` | UC-29 | Whitney |
| GET | `/api/evaluations/report/section` | UC-31 | Whitney |
| GET | `/api/evaluations/report/student/{id}` | UC-33 | Whitney |
| GET | `/api/activities/report/student/{id}` | UC-34 | Whitney |

---

## Key Business Rules (from Use Cases)

| Rule | Description |
|---|---|
| BR-1 | Every team must have at least one instructor assigned |
| BR-2 | Fall active weeks: 5–15. Spring active weeks: 1–15. Winter break = inactive |
| BR-3 | Peer evaluation cannot be edited once completed |
| BR-4 | Students can only submit peer evals for the **previous** week, within a 1-week window |
| BR-5 | Students can only see their own rubric scores, public comments, and overall grade — NOT private comments or evaluator names |

---

## Grade Computation Algorithm (UC-31, UC-33)

```
For each evaluatee in a given week:
  1. Collect all peer evaluations received by that evaluatee for the week
  2. For each evaluation: sum all criterion scores → totalScore
     Example: [10, 9, 10, 9, 10, 10] → totalScore = 58
  3. Average the totalScores across all evaluators → final grade
     Example: (58 + 50) / 2 = 54 out of max total
```
