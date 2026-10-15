# 🏠 RentEase – Rent Management System

### Find Your Perfect Home | List Your Property | Simplify Renting

RentEase is a full-stack web application designed to connect landlords with people looking for rental homes. Users can explore rental properties, compare details, and manage property listings through a simple interface.

## 🌐 Live Deployment

| Component   | Platform | Link                                     |
| ----------- | -------- | ---------------------------------------- |
| Frontend    | Vercel   | https://rentmanagement-black.vercel.app/ |
| Backend API | Render   | https://rentmanagement-1ctb.onrender.com |
| Database    | Aiven    | MySQL Cloud Database                     |

📂 **GitHub Repository:** https://github.com/vikash-vish777/rentmanagement

---

## 📌 Table of Contents

* [About the Project](#-about-the-project)
* [Live Deployment](#-live-deployment)
* [Features](#-features)
* [Technology Stack](#-technology-stack)
* [Project Structure](#-project-structure)
* [Getting Started](#-getting-started)
* [Environment Variables](#-environment-variables)
* [API Overview](#-api-overview)
* [Database](#-database)
* [Deployment Architecture](#-deployment-architecture)
* [Future Enhancements](#-future-enhancements)
* [Author](#-author)

---

## 📖 About the Project

RentEase is a full-stack web application developed to make the process of finding and listing rental properties easier.

The platform supports different types of users, including tenants looking for homes and landlords who want to list their properties.

The application uses React for the frontend, Spring Boot for backend services, and MySQL for data storage.

### 🎯 Project Objectives

* Make rental property discovery easier.
* Allow landlords to publish property listings.
* Provide user registration and login functionality.
* Store and manage application data using a database.
* Connect the frontend and backend through REST APIs.
* Provide a responsive and user-friendly interface.

---

## 🌐 Live Deployment

### 🖥️ Frontend — Vercel

**Live Website:** https://rentmanagement-black.vercel.app/

The React frontend is deployed on Vercel and provides the user interface for interacting with RentEase.

### ⚙️ Backend — Render

**Backend URL:** https://rentmanagement-1ctb.onrender.com

The Java Spring Boot backend is deployed on Render and provides REST APIs for application functionality, including authentication.

The frontend communicates with the backend through HTTP requests.

> **Note:** The backend's root URL may not display a webpage because it is an API service. Individual API endpoints are used by the frontend.

### 🗄️ Database — Aiven

**Database Provider:** Aiven
**Database Engine:** MySQL

RentEase uses a cloud-hosted MySQL database to store application data. The Spring Boot backend connects to the database using configured environment variables.

> Database credentials and private connection details are intentionally not published in this README.

---

## ✨ Features

### 👤 User Authentication

* User registration and login.
* JWT-based authentication.
* Password encryption.
* Role-based access for different types of users.

### 🏡 Property Management

* Create rental property listings.
* Add property details such as title, location, rent, and description.
* Upload property images.
* View available property listings.
* Manage listings according to user permissions.

### 🔎 Property Details

* View property information.
* Explore rental prices and locations.
* Review property details before making a rental decision.

### 🛡️ Role-Based Access

* **Tenant:** Explore properties and find suitable rental homes.
* **Landlord:** Create and manage property listings.
* **Admin:** Access administrative functionality.

*The availability of individual features depends on the current implementation and deployment configuration.*

---

## 🛠️ Technology Stack

### Frontend

* React.js
* JavaScript
* HTML5
* CSS3
* Vite

### Backend

* Java
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* REST APIs
* JWT Authentication
* Maven

### Database

* MySQL
* Aiven Cloud MySQL

### Deployment and Development Tools

* **Vercel** — Frontend deployment
* **Render** — Backend deployment
* **Aiven** — Cloud database hosting
* **Git and GitHub** — Version control and source code management
* **Postman** — API testing
* **Visual Studio Code** — Development environment
* **Docker** — Backend containerization configuration

---

## 📂 Project Structure

```text
rentmanagement/
│
├── backend/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/rentease/
│   │       │       ├── config/
│   │       │       ├── controller/
│   │       │       ├── dto/
│   │       │       ├── model/
│   │       │       ├── repository/
│   │       │       ├── security/
│   │       │       └── service/
│   │       │
│   │       └── resources/
│   │           └── application.properties
│   │
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── components/
│   │   ├── App.jsx
│   │   └── ...
│   ├── package.json
│   └── vite.config.js
│
└── README.md
```

*This is a representative structure. Your actual project may contain additional files and folders.*

---

## 🚀 Getting Started

Follow these steps to run RentEase on your local computer.

### Prerequisites

Install the following software:

* Java Development Kit (JDK 17 or compatible version)
* Node.js and npm
* MySQL database or an accessible MySQL cloud instance
* Git
* Visual Studio Code or another code editor

### 1. Clone the Repository

Open your terminal and run:

```bash
git clone https://github.com/vikash-vish777/rentmanagement.git
```

Navigate to the project directory:

```bash
cd rentmanagement
```

### 2. Configure the Backend

Navigate to the backend directory:

```bash
cd backend
```

Configure the database connection and required environment variables before starting the backend.

Build the application:

```bash
mvn clean package
```

Run the Spring Boot application:

```bash
java -jar target/*.jar
```

The backend normally runs locally at:

```text
http://localhost:8080
```

### 3. Configure the Frontend

Open another terminal in the project directory and navigate to the frontend:

```bash
cd rentmanagement/frontend
```

Install the required dependencies:

```bash
npm install
```

Create a `.env` file inside the `frontend` directory and add:

```env
VITE_API_URL=http://localhost:8080/api
```

Start the frontend development server:

```bash
npm run dev
```

Open the local URL displayed in your terminal. With the default Vite configuration, it is usually:

```text
http://localhost:5173
```

---

## 🔐 Environment Variables

Configure the following environment variables for the backend.

### Backend Environment Variables

```env
SPRING_DATASOURCE_URL=jdbc:mysql://YOUR_DB_HOST:YOUR_DB_PORT/YOUR_DB_NAME?sslMode=REQUIRED
SPRING_DATASOURCE_USERNAME=YOUR_DB_USERNAME
SPRING_DATASOURCE_PASSWORD=YOUR_DB_PASSWORD
JWT_SECRET=YOUR_SECURE_JWT_SECRET
```

Replace the example values with your actual database connection details and secure JWT secret.

### Frontend Environment Variables

For local development:

```env
VITE_API_URL=http://localhost:8080/api
```

For the deployed frontend on Vercel:

```env
VITE_API_URL=https://rentmanagement-1ctb.onrender.com/api
```

Configure the production value in:

**Vercel → Project → Settings → Environment Variables**

After changing the environment variable, redeploy the frontend for the change to take effect.

### ⚠️ Security Notes

* Never commit database passwords or JWT secrets to GitHub.
* Store production credentials in the hosting provider's environment-variable settings.
* Use strong, unique secrets in production.
* Never expose database credentials through frontend environment variables.
* If a secret has been exposed publicly, rotate it immediately.

---

## 🔗 API Overview

The backend provides REST API endpoints for authentication and other application functionality.

### Authentication Endpoints

| Method | Endpoint             | Purpose                                   |
| ------ | -------------------- | ----------------------------------------- |
| POST   | `/api/auth/register` | Register a new user                       |
| POST   | `/api/auth/login`    | Authenticate a user                       |
| GET    | `/api/auth/me`       | Retrieve the authenticated user's profile |

### Example API URLs

**Register:**

```text
https://rentmanagement-1ctb.onrender.com/api/auth/register
```

**Login:**

```text
https://rentmanagement-1ctb.onrender.com/api/auth/login
```

**Current User Profile:**

```text
https://rentmanagement-1ctb.onrender.com/api/auth/me
```

The registration and login endpoints use POST requests. The profile endpoint requires authentication.

Additional endpoints may handle property listings and other functionality according to the backend implementation.

---

## 🗄️ Database

RentEase uses **MySQL**, hosted on **Aiven Cloud**, for persistent data storage.

### Database Configuration

| Property                 | Details                               |
| ------------------------ | ------------------------------------- |
| Database Engine          | MySQL                                 |
| Hosting Provider         | Aiven                                 |
| Backend Integration      | Spring Data JPA                       |
| ORM                      | Hibernate                             |
| Connection Configuration | Backend environment variables         |
| Connection Security      | SSL-enabled connection, as configured |

### How the Database Works

1. A user interacts with the RentEase frontend.
2. The frontend sends an HTTP request to the Spring Boot backend.
3. The backend processes the request and applies the relevant business logic.
4. Spring Data JPA and Hibernate interact with MySQL.
5. The backend returns the appropriate response to the frontend.

### Database Security

* Database credentials are configured on the backend hosting service.
* The database should not be accessed directly from the frontend.
* Production connections should use the database provider's recommended security settings.
* Database credentials must remain private.

**Note:** The exact tables and relationships depend on the entities implemented in the project.

---

## 🏗️ Deployment Architecture

RentEase uses three main services:

```text
             USER
               |
               v
      +------------------+
      | React Frontend   |
      |      Vercel      |
      +------------------+
               |
               | HTTPS / REST API
               v
      +------------------+
      | Spring Boot API  |
      |      Render      |
      +------------------+
               |
               | JDBC / SSL
               v
      +------------------+
      |   MySQL Database |
      |      Aiven       |
      +------------------+
```

### Deployment Flow

1. **Frontend:** The React application is built and deployed on Vercel.
2. **Backend:** The Spring Boot application runs on Render.
3. **Database:** MySQL is hosted on Aiven.
4. **Communication:** The frontend sends API requests to the backend.
5. **Data Storage:** The backend reads and writes data in MySQL.

### Deployment Links

* 🌐 **Live Frontend:** https://rentmanagement-black.vercel.app/
* ⚙️ **Backend Service:** https://rentmanagement-1ctb.onrender.com
* 💻 **Source Code:** https://github.com/vikash-vish777/rentmanagement

---

## 🔮 Future Enhancements

Potential improvements for the project include:

* Advanced property search and filtering.
* Location-based property discovery.
* Interactive maps.
* Property comparison.
* Booking and rental request management.
* Notifications for tenants and landlords.
* Improved admin dashboard and analytics.
* Automated testing and continuous integration.
* Enhanced security and production monitoring.

---

## 👨‍💻 Author

**Vikash Vishwakarma**

BCA Student | Aspiring Full-Stack Developer

### Technical Interests

* Java
* Spring Boot
* React.js
* MySQL
* REST API Development
* Full-Stack Web Development
* Artificial Intelligence and Machine Learning

### Connect With Me

* **GitHub Profile:** https://github.com/vikash-vish777
* **Project Repository:** https://github.com/vikash-vish777/rentmanagement

---

## 📄 License

This project is available for educational and portfolio purposes. Add a license file to the repository if you want to specify formal permissions for reuse and distribution.

---

Thank you for visiting RentEase.
