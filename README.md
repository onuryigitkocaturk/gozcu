# Gözcü — Getting Started

## 1. Prerequisite: Docker services

Postgres (the app's own DB + the monitored mock DB) and MailHog (fake SMTP), from the project root:

```bash
docker compose up -d
```

Check: `docker ps` should show `query-monitor-postgres` and `query-monitor-mailhog` running.

## 2. Backend

In `backend/`, the app won't start without the `JWT_SECRET` environment variable set:

```bash
cd backend
export JWT_SECRET=$(openssl rand -base64 48)
./mvnw spring-boot:run
```

Once up: `http://localhost:8080`

## 3. Frontend

In `frontend/` (run `npm install` the first time only):

```bash
cd frontend
npm install   # first time only
npm run dev
```

Once up: `http://localhost:5173`

## 4. Other interfaces

- **MailHog** (view sent emails): `http://localhost:8025`
- The **backend** uses default connection settings for `monitored_db` (the monitored mock database) and `query_monitor` (the app's own DB); to point at a different database, set the `MONITORED_DB_URL` / `MONITORED_DB_USERNAME` / `MONITORED_DB_PASSWORD` environment variables.

## 5. First admin user

Any user who registers through the frontend (`Kayıt olun`) starts out with the `USER` role. Promote the first admin via SQL:

```sql
UPDATE users SET role='ADMIN' WHERE username='<username>';
```
