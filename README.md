# 🧠 SecondBrain — Personal AI Life OS

> A full-stack AI-powered productivity system that thinks with you. Built with Angular, Spring Boot, PostgreSQL, and Google Gemini.

![AI Chat](docs/screenshots/ai_chat.png)

---

## ✨ What is SecondBrain?

SecondBrain is a personal knowledge and task management system with a built-in AI reasoning layer. Instead of a simple chatbot, it uses **Retrieval-Augmented Generation (RAG)** to give the AI real context about your tasks and notes — so it can actually help you think, not just respond.

Ask it things like:
- *"What are my high priority tasks this week?"*
- *"Summarize my note about Project X"*
- *"Create a task to call John tomorrow at 5 PM"*

---

## 🚀 Features

### 🤖 AI Assistant (Gemini 2.5 Flash Lite)
- Real-time streaming responses via **Server-Sent Events (SSE)**
- **RAG pipeline** — AI reads your actual tasks and notes before answering
- **Decision Engine** — routes queries intelligently (AI / Hybrid / Direct DB)
- **Semantic search** using vector embeddings stored in PostgreSQL
- Persistent chat history that survives server restarts
- Agentic actions — AI can create tasks directly from chat (with confirmation)
- Time-aware greetings (Good morning / afternoon / evening / night)

### ✅ Task Management
- Create, edit, delete tasks
- Priority levels — Low / Medium / High with color badges
- Due dates with overdue highlighting
- Status lifecycle — Todo → In Progress → Done
- "Active today" count

### 📝 Knowledge Base (Notes)
- Rich note editor with title + content
- AI Assistant sidebar per note — Summarize, Explain, Extract actions, Improve writing
- Semantic search across all notes
- Auto-embedding on save for RAG retrieval

### 🔐 Authentication
- JWT-based auth with secure token storage
- Auto-login after registration
- Per-user data isolation — users only see their own data
- Rate limiting on auth endpoints

### 🎨 UI/UX
- Premium **Glassmorphism** dark theme
- Light / Dark mode toggle with OS preference detection
- Responsive layout (desktop + tablet)
- Real-time streaming text with typing cursor animation
- Suggested prompt chips for new users

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Frontend | Angular 12, TypeScript, Vanilla CSS |
| Backend | Spring Boot 3.4, Java 17 |
| Database | PostgreSQL |
| AI | Google Gemini 2.5 Flash Lite |
| Auth | JWT (JSON Web Tokens) |
| Streaming | Server-Sent Events (SSE) |
| Embeddings | Gemini Embedding API + DoubleListConverter |
| Build | Maven, Angular CLI |

---

## 📸 Screenshots

| Login | Tasks |
|---|---|
| ![Login](docs/screenshots/login.png) | ![Tasks](docs/screenshots/tasks.png) |

| Notes | Note Detail + AI |
|---|---|
| ![Notes](docs/screenshots/notes.png) | ![Note Detail](docs/screenshots/note_detail.png) |

| AI Chat | AI Task Creation |
|---|---|
| ![AI Chat](docs/screenshots/ai_chat.png) | ![AI Action](docs/screenshots/ai_chat_action.png) |

---

## 🏗️ Architecture

```
secondBrain/
├── backend/                          # Spring Boot application
│   └── src/main/java/com/example/backend/
│       ├── ai/                       # AI orchestration layer
│       │   ├── AiChatService.java    # Main AI pipeline
│       │   ├── DecisionEngineService.java  # Intent routing
│       │   ├── ContextBuilderService.java  # RAG context assembly
│       │   ├── GeminiService.java    # Gemini API client (SSE)
│       │   ├── EmbeddingService.java # Vector embeddings
│       │   ├── SafetyService.java    # Input sanitization
│       │   ├── SemanticCacheService.java   # Response caching
│       │   └── PromptBuilder.java    # System prompt construction
│       ├── controller/               # REST API endpoints
│       ├── service/                  # Business logic
│       ├── model/                    # JPA entities
│       ├── repository/               # Spring Data repositories
│       ├── security/                 # JWT auth + rate limiting
│       └── dto/                      # Data transfer objects
│
└── frontend/                         # Angular application
    └── src/app/
        ├── features/
        │   ├── ai-chat/              # AI chat page + streaming
        │   ├── tasks/                # Task management
        │   ├── notes/                # Notes list + detail editor
        │   └── auth/                 # Login + Register
        └── core/
            ├── guards/               # Auth route guards
            └── interceptors/         # JWT HTTP interceptor
```

---

## ⚙️ Getting Started

### Prerequisites
- Java 17+
- Node.js 18+
- PostgreSQL 14+
- Google Gemini API key ([Get one free here](https://aistudio.google.com/))

### 1. Clone the repo
```bash
git clone https://github.com/tanmayythakare/secondbrain.git
cd secondbrain
```

### 2. Set up the database
```sql
CREATE DATABASE secondbrain_db;
CREATE USER secondbrain_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE secondbrain_db TO secondbrain_user;
```

### 3. Configure the backend
Edit `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/secondbrain_db
spring.datasource.username=secondbrain_user
spring.datasource.password=your_password

jwt.secret=your_jwt_secret_key_here

app.ai.gemini.key=your_gemini_api_key_here
app.ai.gemini.model=gemini-2.5-flash-lite
app.ai.gemini.endpoint=https://generativelanguage.googleapis.com/v1beta/models/
```

### 4. Run the backend
```bash
cd backend
./mvnw spring-boot:run
```
Backend runs on `http://localhost:8080`

### 5. Run the frontend
```bash
cd frontend/secondbrain-frontend
npm install
ng serve
```
Frontend runs on `http://localhost:4200`

### 6. Open the app
Visit `http://localhost:4200` — register an account and start using SecondBrain.

---

## 🔌 API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login, returns JWT |
| GET | `/api/tasks` | Get all tasks |
| POST | `/api/tasks` | Create task |
| PUT | `/api/tasks/{id}` | Update task |
| DELETE | `/api/tasks/{id}` | Delete task |
| GET | `/api/notes` | Get all notes |
| POST | `/api/notes` | Create note |
| PUT | `/api/notes/{id}` | Update note |
| DELETE | `/api/notes/{id}` | Delete note |
| GET | `/api/notes/search?q=` | Semantic search notes |
| POST | `/api/ai/chat/stream` | AI chat (SSE stream) |
| GET | `/api/ai/chat/history` | Get chat history |
| POST | `/api/ai/chat/confirm` | Execute agentic action |

---
## 🧠 How the AI Works

```
User Message
     │
     ▼
SafetyService ──── (blocks harmful input)
     │
     ▼
DecisionEngine ─── (AI / Hybrid / Direct DB / Action)
     │
     ├── NON_AI ──► Direct DB query → instant response
     │
     ├── ACTION ──► Extract payload → Confirm button → Execute
     │
     └── AI/HYBRID ──► ContextBuilder (RAG)
                              │
                        ┌─────┴──────┐
                     Recent      Semantic
                     Items       Search
                        └─────┬──────┘
                              │
                         PromptBuilder
                              │
                         GeminiService
                         (SSE Stream)
                              │
                         Frontend renders
                         token by token
```


---

## 🔒 Security

- All endpoints (except `/api/auth/**`) require a valid JWT
- Passwords hashed with BCrypt
- Rate limiting on auth and AI endpoints
- Input sanitization before AI processing
- Per-user data isolation enforced at service layer
- Environment variables for all secrets

---

## 📋 What I Learned Building This

This project was built to learn and demonstrate:

- **Full-stack development** with Angular + Spring Boot
- **AI integration** — not just calling an API, but building a proper RAG pipeline
- **Streaming UIs** — Server-Sent Events for real-time token streaming
- **Vector embeddings** — storing and querying semantic vectors in PostgreSQL
- **JWT authentication** — stateless auth with Spring Security
- **Production practices** — error handling, rate limiting, data isolation, environment config

---

## 👤 Author

**Tanmay Thakare**
- GitHub: [@tanmayythakare](https://github.com/tanmayythakare)

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

<p align="center">Built with ☕ and a lot of debugging</p>