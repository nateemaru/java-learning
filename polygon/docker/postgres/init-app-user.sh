#!/bin/bash

set -euo pipefail

psql \
  --username "$POSTGRES_USER" \
  --dbname "$POSTGRES_DB" \
  --set=app_username="$DB_APP_USERNAME" \
  --set=app_password="$DB_APP_PASSWORD" \
  --set=app_database="$POSTGRES_DB" <<'SQL'

SELECT format(
    'CREATE USER %I WITH PASSWORD %L',
    :'app_username',
    :'app_password'
)
WHERE NOT EXISTS (
    SELECT 1
    FROM pg_roles
    WHERE rolname = :'app_username'
)
\gexec

ALTER DATABASE :"app_database" OWNER TO :"app_username";

GRANT ALL PRIVILEGES
    ON DATABASE :"app_database"
    TO :"app_username";

SQL