#!/bin/bash
# Project Pulse — local dev setup script
# Run from the repo root: bash setup-dev.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
MIGRATIONS="$SCRIPT_DIR/backend/src/main/resources/db/migration"

echo ""
echo "=== Project Pulse Dev Setup ==="
echo ""

# ── 1. Check Java 21 ────────────────────────────────────────────────────────
if ! java -version 2>&1 | grep -q "21"; then
  echo "ERROR: Java 21 is required."
  echo "  brew install openjdk@21"
  echo "  Then add to your shell profile:"
  echo "  export JAVA_HOME=/usr/local/opt/openjdk@21"
  echo "  export PATH=\"\$JAVA_HOME/bin:\$PATH\""
  exit 1
fi
echo "✓ Java 21 found"

# ── 2. Check MySQL ───────────────────────────────────────────────────────────
if ! command -v mysql &>/dev/null; then
  echo "ERROR: MySQL is required."
  echo "  brew install mysql && brew services start mysql"
  exit 1
fi
echo "✓ MySQL found"

# ── 3. Check Node ────────────────────────────────────────────────────────────
if ! command -v node &>/dev/null; then
  echo "ERROR: Node.js is required."
  echo "  brew install node"
  exit 1
fi
echo "✓ Node.js found"

# ── 4. Create database and run migrations ───────────────────────────────────
echo ""
echo "Setting up database..."

mysql -u root --password="" 2>/dev/null <<EOF || mysql -u root <<EOF2
CREATE DATABASE IF NOT EXISTS projectpulse_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE projectpulse_dev;
SOURCE $MIGRATIONS/V1__create_users_table.sql;
SOURCE $MIGRATIONS/V2__create_rubrics_table.sql;
SOURCE $MIGRATIONS/V3__create_sections_and_weeks_table.sql;
SOURCE $MIGRATIONS/V4__create_teams_table.sql;
SOURCE $MIGRATIONS/V5__create_activities_table.sql;
SOURCE $MIGRATIONS/V6__create_evaluations_table.sql;
INSERT IGNORE INTO peer_evaluation_user (username, first_name, last_name, password, roles, enabled)
VALUES ('admin@test.com', 'Admin', 'User',
        '\$2a\$10\$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
        'ROLE_ADMIN', 1);
EOF
CREATE DATABASE IF NOT EXISTS projectpulse_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE projectpulse_dev;
SOURCE $MIGRATIONS/V1__create_users_table.sql;
SOURCE $MIGRATIONS/V2__create_rubrics_table.sql;
SOURCE $MIGRATIONS/V3__create_sections_and_weeks_table.sql;
SOURCE $MIGRATIONS/V4__create_teams_table.sql;
SOURCE $MIGRATIONS/V5__create_activities_table.sql;
SOURCE $MIGRATIONS/V6__create_evaluations_table.sql;
INSERT IGNORE INTO peer_evaluation_user (username, first_name, last_name, password, roles, enabled)
VALUES ('admin@test.com', 'Admin', 'User',
        '\$2a\$10\$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
        'ROLE_ADMIN', 1);
EOF2

echo "✓ Database ready"

# ── 5. Install frontend deps ─────────────────────────────────────────────────
echo ""
echo "Installing frontend dependencies..."
cd "$SCRIPT_DIR/frontend" && npm install --silent
echo "✓ Frontend dependencies installed"

echo ""
echo "=== Setup complete! ==="
echo ""
echo "To start the app, open two terminals:"
echo ""
echo "  Terminal 1 — Backend:"
echo "    cd backend"
echo "    JAVA_HOME=/usr/local/opt/openjdk@21 mvn spring-boot:run -Dspring-boot.run.profiles=dev"
echo ""
echo "  Terminal 2 — Frontend:"
echo "    cd frontend"
echo "    npm run dev"
echo ""
echo "Then open: http://localhost:5173"
echo "Login:     admin@test.com / password"
echo ""
