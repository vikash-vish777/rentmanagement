# RentEase

RentEase is a room and house rental platform with a React frontend and a Java 17 / Spring Boot REST API. It supports tenant, landlord, and administrator workflows backed by MySQL.

## Features

- Account registration and JWT sign-in with tenant and landlord roles
- Property discovery with location, rent, home type, bedroom, and furnishing filters
- Property details, photo galleries, map links, reviews, favorites, and side-by-side comparison
- Landlord listing creation, editing, availability management, and multi-photo uploads
- Tenant booking requests and landlord accept/reject workflows
- Tenant-to-landlord messaging and conversation history
- Admin overview, reporting, account moderation, listing moderation, and booking visibility

## Run locally

### 1. Start MySQL

Create or use a MySQL 8 database. The backend JDBC URL is configured in `backend/src/main/resources/application.properties`; the configured URL creates the `rentease_db` database when the MySQL account has permission.

### 2. Start the API

Install JDK 17 and Maven, configure the MySQL username and password in `backend/src/main/resources/application.properties`, then run:

```powershell
cd backend
mvn spring-boot:run
```

The API runs at `http://localhost:8080`. The development data initializer creates sample accounts and homes when the database has no users. Sample accounts are for local development only:

| Role | Email | Password |
| --- | --- | --- |
| Admin | `admin@rentease.com` | `Admin@123` |
| Landlord | `landlord@rentease.com` | `Landlord@123` |
| Tenant | `tenant@rentease.com` | `Tenant@123` |

Change or remove the seeded credentials before deploying the application.

### 3. Start the web app

Install Node.js 20.19+ or 22.12+ and npm, then run:

```powershell
cd frontend
npm install
npm run dev
```

Open the local Vite URL printed in the terminal. The web app connects to `http://localhost:8080/api` by default. Set `VITE_API_URL` to override the API base URL.

## Checks

From `frontend`:

```powershell
npm run build
npm run lint
```

From `backend`:

```powershell
mvn test
```

## API areas

The REST API is organized under `/api`: `/auth`, `/properties`, `/bookings`, `/favorites`, `/messages`, `/reviews`, and `/admin`. JWT-protected routes require an `Authorization: Bearer <token>` header. Uploaded property images are stored by the backend and served from its uploads route.

## 🏠 Rent Management System

**🌐 Live Demo:** [Visit Rent Management System](https://rentmanagement-black.vercel.app/)

Click the link above to explore the deployed application.
