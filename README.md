# Project Pulse

Peer evaluation and weekly activity reporting platform for senior design courses.
TCU Web Technologies — Spring 2026 — Dr. Wei

---

## Local Dev Setup

### Prerequisites
- **Java 21** — `brew install openjdk@21`
- **Maven** — `brew install maven`
- **MySQL 9.x** — `brew install mysql && brew services start mysql`
- **Node.js 18+** — `brew install node`

After installing Java 21, add to your shell profile (`~/.zshrc` or `~/.bash_profile`):
```bash
export JAVA_HOME=/usr/local/opt/openjdk@21
export PATH="$JAVA_HOME/bin:$PATH"
```

### One-time setup
Clone the repo, then from the project root:
```bash
bash setup-dev.sh
```
This creates the database, runs all migrations, and seeds the admin account.

### Starting the app
Open two terminals from the project root:

**Terminal 1 — Backend**
```bash
cd backend
JAVA_HOME=/usr/local/opt/openjdk@21 mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Terminal 2 — Frontend**
```bash
cd frontend
npm run dev
```

Open **http://localhost:5173**

**Default login:** `admin@test.com` / `password`

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 4.0.2, Java 21, Spring Security + JWT |
| Frontend | Vue 3, TypeScript, Vuetify 3, Vite |
| Database | MySQL — schema managed via Flyway migrations (run manually in dev) |
| Auth | JWT — stored in localStorage, role-based (`ROLE_ADMIN`, `ROLE_INSTRUCTOR`, `ROLE_STUDENT`) |

---

## Team

| Member | Use Cases | Areas |
|---|---|---|
| Josh | Auth, UC-1–6, UC-25/26/30 | Security, Rubrics, Sections |
| Oscar | UC-7–17, UC-27, UC-32 | Teams, Students, WAR |
| Whitey | UC-18–24, UC-28–29, UC-31, UC-33–34 | Instructors, Peer Evaluations |

See [TEAM_REFERENCE.md](TEAM_REFERENCE.md) for full details.
