# 🧠 Second Brain — The Ultimate Project Manual

## 1. Project Vision & Philosophy
**Life OS** is a "Second Brain" productivity ecosystem designed to centralize tasks and notes into a unified knowledge graph, managed by an **Unbreakable AI Orchestration Layer**. 

The core philosophy is **"Deterministic when possible, Intelligent when required."** We minimize AI costs and hallucination risks by using a multi-layered reasoning engine that handles routine tasks via backend logic and saves the AI for complex reasoning.

---

## 2. The "Unbreakable" AI Architecture
This is the heart of the project. We have moved beyond simple "Call AI" logic to a production-grade orchestration pipeline.

### 🛡️ Layer 1: Input & Safety
- **SafetyService**: Detects and rejects prompt injection attempts (e.g., "ignore previous instructions").
- **Decision Engine**: A deterministic gatekeeper that routes simple CRUD requests (e.g., "list my tasks") to the backend directly, bypassing the AI to save tokens and time.

### 🧠 Layer 2: Context Intelligence
- **3-Tier Retrieval**:
    - **Tier 1 (Recent)**: The latest 5 tasks and notes.
    - **Tier 2 (Keyword)**: Items matching exact keywords from the user's query.
    - **Tier 3 (Semantic Search)**: Vector-based similarity search using **Gemini Embeddings** (software-fallback for pgvector) to find items by meaning.
- **Context Budgeting**: Automatically trims data to stay within token limits without losing relevance.

### ⚡ Layer 3: Reasoning & Streaming
- **Gemini 1.5 Flash**: Orchestrates high-speed reasoning.
- **SSE Streaming**: Responses are delivered token-by-token for a "live typing" experience.
- **Semantic Caching**: Stores responses for similar queries to deliver sub-5ms response times for repeat questions.

### 🛠️ Layer 4: Output Validation & Recovery
- **JSON Auto-Fixer**: Automatically heals malformed AI responses (trailing commas, missing brackets).
- **Hallucination Guard**: Cross-verifies every ID mentioned by the AI against the database to prevent "made up" links.
- **4-Level Recovery**: If a response fails, the system escalates through retries with expanded context before returning an error.

---

## 3. Technical Stack
### Backend (Spring Boot 3.4+)
- **Core**: Java 17, Spring WebMVC + WebFlux (Reactive Streaming).
- **Database**: PostgreSQL (Relational + Array-based vector storage).
- **AI**: Google Gemini API (Reasoning + Embeddings).
- **Security**: JWT-based stateless authentication with strict ownership validation.

### Frontend (Angular 12)
- **UI/UX**: Premium **Glassmorphism** design system with a dark-first aesthetic.
- **Real-time**: Reactive `fetch` stream handling for live AI typing.
- **State**: RxJS-based state management for tasks and notes.

---

## 4. Key Functional Features
- **Task Management**: Full CRUD with priority, status, and semantic linking.
- **Note Management**: Rich text notes with automated background embedding generation.
- **AI Chat**: An interactive interface that "links" to your real data.
- **Audit & Feedback**: Every AI action is logged for performance analysis, and users can provide 👍/👎 feedback to improve the system.

---

## 5. Directory Map
### Root Structure
- `/backend`: Maven Spring Boot project.
- `/frontend`: Angular project.
- `ROADMAP.md`: The architectural journey.
- `PROJECT_INFO.md`: This manual (Source of Truth).

### Core Components (`/backend/src/main/java/com/example/backend/ai`)
- `AiChatService.java`: The central orchestrator.
- `DecisionEngineService.java`: The deterministic router.
- `GeminiService.java`: The AI client (Streaming + Blocking).
- `ContextBuilderService.java`: The multi-tier RAG (Retrieval Augmented Generation) engine.
- `SemanticCacheService.java`: High-speed memory for repeat queries.
- `AiAuditLogRepository.java`: Persistence for all AI interactions.

---

## 6. Project Status (Production-Ready)
- [x] **Core CRUD**: Tasks & Notes functional.
- [x] **AI Orchestration**: Multi-tier context, safety, and recovery fully implemented.
- [x] **Performance**: Semantic caching and SSE streaming active.
- [x] **Reliability**: Self-healing JSON and Hallucination guards active.
- [x] **Observability**: Audit logging and user feedback loop integrated.

---

## 7. How to Run
1. **Database**: Start PostgreSQL and create `secondbrain_db`.
2. **Backend**: Provide `gemini.api.key` in `application.properties`. Run `./mvnw spring-boot:run`.
3. **Frontend**: Run `npm start` in the frontend directory.

---
**Document Updated:** 2026-05-01 | **Status:** Mission-Critical Complete
