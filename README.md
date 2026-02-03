# 🧠 Life OS / Second Brain

> A production-ready full-stack productivity platform demonstrating enterprise-grade architecture, security practices, and scalable design patterns.

[![Live Demo](https://img.shields.io/badge/demo-live-success)](https://thisissecondbrain.netlify.app)
[![API Docs](https://img.shields.io/badge/API-Swagger-blue)](https://secondbrain-c9bn.onrender.com/swagger-ui.html)
[![License](https://img.shields.io/badge/license-MIT-green)]()

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Live Demo](#-live-demo)
- [Key Features](#-key-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Security](#-security)
- [Getting Started](#-getting-started)
- [API Documentation](#-api-documentation)
- [Deployment](#-deployment)
- [Roadmap](#-roadmap)

---

## 🎯 Overview

Life OS is a full-stack personal knowledge management and productivity application built to showcase modern web development practices. The project emphasizes:

- **Clean Architecture** – Separation of concerns with clear layering
- **Security-First Design** – JWT authentication, BCrypt hashing, CORS configuration
- **Production Deployment** – Real-world hosting with automatic CI/CD
- **RESTful API Design** – Comprehensive documentation and error handling
- **Scalable Backend** – Multi-user support with strict data isolation

This is a **portfolio-grade project** built to demonstrate end-to-end full-stack development capabilities.

---

## 🔗 Live Demo

| Service | URL | Description |
|---------|-----|-------------|
| **Frontend** | [thisissecondbrain.netlify.app](https://thisissecondbrain.netlify.app) | Angular SPA hosted on Netlify |
| **Backend API** | [secondbrain-c9bn.onrender.com](https://secondbrain-c9bn.onrender.com) | Spring Boot REST API |
| **API Docs** | [Swagger UI](https://secondbrain-c9bn.onrender.com/swagger-ui.html) | Interactive API documentation |

---

## ✨ Key Features

### Core Functionality
- ✅ **Task Management** – Create, update, delete, and track tasks
- 📝 **Notes System** – Rich note-taking with search capabilities
- 🔐 **Secure Authentication** – JWT-based stateless authentication
- 👥 **Multi-User Support** – Complete data isolation per user
- 🔍 **Full-Text Search** – Search across notes with PostgreSQL

### Technical Highlights
- 🛡️ **Enterprise Security** – Spring Security with custom JWT filters
- 📊 **RESTful API** – OpenAPI/Swagger documented endpoints
- 🎨 **Responsive UI** – Clean, modern Angular frontend
- 🔄 **Auto-Deploy** – GitHub integration with Netlify/Render
- 🐳 **Container-Ready** – Multi-stage Dockerfile for production

### Experimental Features
- 🕸️ **Knowledge Graph** – Visual exploration of note relationships (beta)

---

## 🛠️ Tech Stack

<table>
<tr>
<td valign="top" width="50%">

### Frontend
- **Framework:** Angular 
- **Language:** TypeScript
- **Styling:** CSS3
- **HTTP Client:** Angular HttpClient
- **State Management:** RxJS
- **Deployment:** Netlify

</td>
<td valign="top" width="50%">

### Backend
- **Framework:** Spring Boot 
- **Security:** Spring Security + JWT
- **ORM:** Spring Data JPA / Hibernate
- **Database:** PostgreSQL
- **Documentation:** Swagger/OpenAPI
- **Deployment:** Render (with Docker)

</td>
</tr>
</table>

---

## 🏗️ Architecture

### High-Level Design

```
┌─────────────────┐         ┌──────────────────┐         ┌──────────────┐
│                 │         │                  │         │              │
│  Angular SPA    │ ◄─────► │  Spring Boot API │ ◄─────► │  PostgreSQL  │
│  (Netlify)      │  HTTPS  │  (Render)        │   JPA   │  (Render)    │
│                 │         │                  │         │              │
└─────────────────┘         └──────────────────┘         └──────────────┘
```

### Backend Architecture

```
┌─────────────────────────────────────────────────────────┐
│                   Spring Boot Application               │
├─────────────────────────────────────────────────────────┤
│  Security Layer                                         │
│  ├─ JWT Authentication Filter                           │
│  ├─ Spring Security Configuration                       │
│  └─ CORS Configuration                                  │
├─────────────────────────────────────────────────────────┤
│  Controller Layer (REST API)                            │
│  ├─ AuthController                                      │
│  ├─ TaskController                                      │
│  └─ NoteController                                      │
├─────────────────────────────────────────────────────────┤
│  Service Layer (Business Logic)                         │
│  ├─ UserService                                         │
│  ├─ TaskService                                         │
│  └─ NoteService                                         │
├─────────────────────────────────────────────────────────┤
│  Repository Layer (Data Access)                         │
│  ├─ UserRepository (JPA)                                │
│  ├─ TaskRepository (JPA)                                │
│  └─ NoteRepository (JPA)                                │
├─────────────────────────────────────────────────────────┤
│  Exception Handling                                     │
│  └─ @ControllerAdvice Global Exception Handler          │
└─────────────────────────────────────────────────────────┘
```

### Key Design Decisions

- **Stateless Authentication** – JWT tokens eliminate server-side session management
- **Repository Pattern** – Clean abstraction over data access
- **DTO Pattern** – Separation between API contracts and domain models
- **Service Layer** – Business logic isolated from controllers
- **Global Exception Handling** – Consistent error responses across all endpoints

---

## 🔐 Security

### Authentication Flow

1. User submits credentials to `/auth/login`
2. Backend validates credentials and generates JWT
3. Client stores JWT and includes it in `Authorization` header
4. Custom filter validates JWT on each request
5. User context populated in Spring Security

### Security Features

| Feature | Implementation |
|---------|----------------|
| **Password Storage** | BCrypt hashing with salt |
| **Token-Based Auth** | Stateless JWT with expiration |
| **Request Filtering** | Custom `OncePerRequestFilter` |
| **Authorization** | Method-level security annotations |
| **CORS** | Configured for trusted origins only |
| **Data Isolation** | User-scoped queries with `SecurityContext` |
| **Environment Security** | Secrets managed via environment variables |

---

## 🚀 Getting Started

### Prerequisites

- **Java 17+**
- **Node.js 18+** and npm
- **PostgreSQL 14+**
- **Maven 3.6+**

### Backend Setup

```bash
# Navigate to backend directory
cd backend

# Configure database (application.properties)
spring.datasource.url=jdbc:postgresql://localhost:5432/secondbrain
spring.datasource.username=your_username
spring.datasource.password=your_password

# Run the application
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`

### Frontend Setup

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
ng serve
```

The application will be available at `http://localhost:4200`

### Docker Setup (Optional)

```bash
# Build and run with Docker
docker build -t secondbrain-backend .
docker run -p 8080:8080 secondbrain-backend
```

---

## 📘 API Documentation

### Swagger UI

Interactive API documentation is available at:
```
https://secondbrain-c9bn.onrender.com/swagger-ui.html
```

### Key Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/login` | Authenticate and receive JWT | ❌ |
| GET | `/api/tasks` | Retrieve user's tasks | ✅ |
| POST | `/api/tasks` | Create new task | ✅ |
| PUT | `/api/tasks/{id}` | Update task | ✅ |
| DELETE | `/api/tasks/{id}` | Delete task | ✅ |
| GET | `/api/notes` | Retrieve user's notes | ✅ |
| POST | `/api/notes` | Create new note | ✅ |
| GET | `/api/notes/search?q={query}` | Search notes | ✅ |

### Error Handling

All errors return consistent JSON responses:

```json
{
  "timestamp": "2024-02-03T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for request",
  "path": "/api/tasks"
}
```

---

## 🚢 Deployment

### Continuous Deployment

- **Frontend:** Automatically deploys to Netlify on push to `main`
- **Backend:** Automatically deploys to Render on push to `main`
- **Database:** Managed PostgreSQL instance on Render

### Environment Variables

```bash
# Backend (.env)
DATABASE_URL=postgresql://...
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000

# Frontend (environment.ts)
API_URL=https://secondbrain-c9bn.onrender.com
```

---

## 🔮 Roadmap

### v1.1 (Planned)
- [ ] Pagination for tasks and notes
- [ ] Advanced filtering and sorting
- [ ] Export functionality (JSON, CSV)
- [ ] Dark mode support

### v2.0 (Future)
- [ ] Real-time collaboration features
- [ ] File attachments for notes
- [ ] Mobile-responsive improvements
- [ ] Analytics dashboard
- [ ] Tags and categories system
- [ ] Habit and goal tracking

### Infrastructure
- [ ] GitHub Actions CI/CD pipeline
- [ ] Automated testing suite
- [ ] Performance monitoring
- [ ] Database backup automation

---

## 📊 Project Status

![Version](https://img.shields.io/badge/version-1.0.0-blue)
![Status](https://img.shields.io/badge/status-production-success)
![Build](https://img.shields.io/badge/build-passing-brightgreen)

**Current Version:** v1.0.0  
**Status:** Production-ready, actively maintained  
**Focus:** Core functionality, security, and deployment stability

---

## 🤝 Contributing

This is a portfolio project, but suggestions and feedback are welcome! Feel free to:

- Open an issue for bugs or feature requests
- Submit a pull request with improvements
- Share feedback on architecture decisions

---

## 📝 License

This project is available under the MIT License. See LICENSE file for details.

---

## 👤 Author

**Built as a portfolio project to demonstrate:**
- Full-stack development expertise
- Modern backend architecture with Spring Boot
- Frontend development with Angular
- DevOps and deployment practices
- Security-first design principles
- RESTful API design and documentation

---

## 🙏 Acknowledgments

- Spring Boot and Angular communities
- Netlify and Render for hosting platforms
- PostgreSQL for reliable database management

---

### 📬 Contact

For questions or opportunities, feel free to reach out:
- LinkedIn: [https://www.linkedin.com/in/tanmaythakare/]
- GitHub: [[@justTanmay][(https://github.com/justTanmay)]]

---

<div align="center">

**⭐ If you find this project helpful, please consider giving it a star!**

Made with ☕ and code

</div>
