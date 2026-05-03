# 🤖 SecondBrain — AI Integration Implementation Prompt
> Feed this entire document into Cursor AI as context before generating any code.

---

## 📌 PROJECT CONTEXT

This is an **EXISTING production codebase**. Do NOT redesign from scratch. ONLY extend and modify existing code.

### Stack
- **Frontend**: Angular (latest stable, use standalone components + signals where possible)
- **Backend**: Spring Boot (Java), package: `com.example.backend`
- **Database**: PostgreSQL
- **Auth**: JWT (interceptor already exists, all API calls go through it)
- **AI Provider**: **Ollama** (local, free tier) — default model: `llama3` or `mistral`
- **API base**: `/api` prefix on all existing endpoints

### Existing Folder Structure

```
root/
├── backend/
│   ├── src/main/java/com/example/backend/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── exception/
│   │   ├── model/
│   │   ├── repository/
│   │   ├── security/
│   │   └── service/
│   ├── src/main/resources/
│   └── src/test/java/com/example/backend/
│
├── frontend/secondbrain-frontend/
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/           (guards, interceptors, auth service)
│   │   │   ├── features/
│   │   │   │   ├── tasks/
│   │   │   │   └── notes/
│   │   │   └── shared/
│   └── (standard Angular structure)
│
└── docs/
```

### Existing API Pattern
All APIs follow `/api/{resource}` format. JWT token is sent via `Authorization: Bearer <token>` header via Angular HTTP interceptor already in place.

---

## 🚨 CRITICAL SYSTEM DESIGN RULES

1. **DB = Source of Truth. AI = Reasoning layer only.**
2. AI must NEVER store full task/note content in memory tables.
3. Compressed memory stores ONLY: `id`, `title`, `due_date` (for tasks) or `id`, `title` (for notes).
4. AI acts as a **Navigator** — it identifies relevant items and returns clickable links. It does NOT explain full old content.
5. Simple queries (e.g., "show pending tasks") → **Backend handles directly, no AI call.**
6. AI is only invoked for: natural language reasoning, summarization, multi-step queries, suggestions.
7. No knowledge graph. No backlinks. No manual linking systems.

---

## 📦 REQUIRED OUTPUT FORMAT

Generate the following sections in order:

---

## 1. PHASE-WISE IMPLEMENTATION PLAN

Break into phases. For EACH phase include:

- 🎯 Goal
- 🔧 Backend changes (exact files)
- 💻 Frontend changes (exact files)
- 🔌 APIs to create/modify
- 🧱 Exact file paths using real project structure above
- 🔗 Dependencies on previous phases

### Suggested Phase Breakdown:

**Phase 1 — MVP: Basic AI Chat**
- Chat endpoint in backend
- Ollama integration service
- Basic context: current tasks (pending/overdue) + last 5 notes titles
- Frontend: `features/ai-chat/` component with message UI
- Response renders as text + clickable items

**Phase 2 — Memory System**
- `ai_chat_history` table
- Short-term: last N full messages
- Mid-term: summarized chat sessions
- Compressed old memory: title + due_date + id only

**Phase 3 — Note Assistant (in-note AI)**
- AI panel on note detail page
- Actions: Summarize, Explain, Extract tasks
- Calls backend with note content as context

**Phase 4 — Advanced (Optional)**
- Embeddings-based semantic search (pgvector or external)
- Auto memory compression scheduler
- Long-term memory (goals, habits, preferences)

---

## 2. BACKEND IMPLEMENTATION (SPRING BOOT)

### New Package: `ai/`
Under `com.example.backend/ai/` create:

**Controller:**
- `AiChatController.java` — POST `/api/ai/chat`

**Service:**
- `AiChatService.java` — orchestrates intent detection, context building, Ollama call
- `OllamaService.java` — HTTP client wrapper for Ollama REST API (`http://localhost:11434/api/generate`)
- `ContextBuilderService.java` — assembles prompt context from DB
- `IntentDetectorService.java` — rule-based: detects if query needs AI or direct DB response

**Helper/Util:**
- `PromptBuilder.java` — builds final prompt string from template + context

**DTOs (in `dto/ai/`):**
- `ChatRequestDto.java` — `{ message: String }`
- `ChatResponseDto.java` — `{ message: String, items: List<LinkedItemDto> }`
- `LinkedItemDto.java` — `{ id, type (task|note), title, dueDate }`

### New Database Tables

```sql
-- Stores individual chat messages
CREATE TABLE ai_chat_messages (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id),
  session_id UUID NOT NULL,
  role VARCHAR(10) NOT NULL, -- 'user' or 'assistant'
  content TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT NOW()
);

-- Compressed memory (old sessions)
CREATE TABLE ai_memory_compressed (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id),
  item_type VARCHAR(10) NOT NULL, -- 'task' or 'note'
  item_id BIGINT NOT NULL,
  title VARCHAR(500),
  due_date DATE, -- null for notes
  mentioned_at TIMESTAMP DEFAULT NOW()
);

-- Chat session summaries (mid-term memory)
CREATE TABLE ai_chat_summaries (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id),
  session_id UUID NOT NULL,
  summary TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT NOW()
);
```

Use Flyway migration files in `src/main/resources/db/migration/`.

### Method-Level Breakdown

**`AiChatService.processMessage(userId, message)`**
1. Call `IntentDetectorService.detect(message)` → returns enum: `DIRECT_QUERY` or `AI_REQUIRED`
2. If `DIRECT_QUERY`: query DB directly, format `ChatResponseDto`, return. No Ollama call.
3. If `AI_REQUIRED`:
   a. `ContextBuilderService.build(userId, message)` → assembles context string
   b. `PromptBuilder.build(context, message)` → final prompt
   c. `OllamaService.generate(prompt)` → raw AI text
   d. Parse response into `ChatResponseDto`
   e. Save to `ai_chat_messages`
   f. Return response

**`ContextBuilderService.build(userId, message)`**
- Fetch: pending/overdue tasks (title, dueDate, id)
- Fetch: last 5 notes (title, id only)
- Fetch: last 10 chat messages from `ai_chat_messages` (short-term memory)
- Fetch: compressed memory from `ai_memory_compressed` (title + due_date only)
- Return assembled context string (keep under 2000 tokens)

**`IntentDetectorService.detect(message)`**
- Keywords for DIRECT_QUERY: "pending", "overdue", "today", "list my tasks", "show notes"
- Everything else → AI_REQUIRED

**`OllamaService.generate(prompt)`**
- POST to `http://localhost:11434/api/generate`
- Body: `{ "model": "llama3", "prompt": "...", "stream": false }`
- Return response string

---

## 3. FRONTEND IMPLEMENTATION (ANGULAR)

### New Feature Module: `features/ai-chat/`

```
frontend/secondbrain-frontend/src/app/features/ai-chat/
├── ai-chat.component.ts
├── ai-chat.component.html
├── ai-chat.component.scss
├── message-bubble/
│   ├── message-bubble.component.ts
│   └── message-bubble.component.html
└── ai-chat.service.ts
```

**`ai-chat.service.ts`**
- `sendMessage(message: string): Observable<ChatResponse>` → POST `/api/ai/chat`
- `getChatHistory(): Observable<Message[]>` → GET `/api/ai/chat/history`

**`ai-chat.component.ts`**
- Signal-based state: `messages = signal<Message[]>([])`
- `sendMessage()` method
- On response: append to messages, handle `items` array for clickable links

**`message-bubble.component.html`** — render:
- User messages: right-aligned bubble
- AI messages: left-aligned, text + list of clickable items
- Clickable items navigate to `/tasks/:id` or `/notes/:id` using Angular Router

**Add route** in app routing:
```typescript
{ path: 'ai', component: AiChatComponent, canActivate: [AuthGuard] }
```

**Update navbar** to add "AI" tab linking to `/ai`.

---

## 4. AI SYSTEM DESIGN

### Prompt Template

```
You are a personal AI assistant for a productivity app called SecondBrain.
You have access to the user's current tasks and notes context below.
Answer concisely. When referencing a task or note, include its ID and title so the UI can make it clickable.
Never invent tasks or notes not in the context. If you don't know, say so.

=== CONTEXT START ===
[PENDING TASKS]
{task_list: id | title | due_date}

[RECENT NOTES]
{note_list: id | title}

[RECENT CHAT]
{last_10_messages}

[COMPRESSED MEMORY]
{old_items: id | title | due_date}
=== CONTEXT END ===

User: {user_message}
Assistant:
```

### Response Format AI Must Follow

Instruct AI in prompt to respond in this JSON format:
```json
{
  "message": "You have 2 pending tasks.",
  "items": [
    { "id": 12, "type": "task", "title": "Finish report", "dueDate": "2026-05-01" },
    { "id": 7, "type": "note", "title": "Project Phoenix Roadmap", "dueDate": null }
  ]
}
```

### What is INCLUDED vs EXCLUDED in Context

| Data | Included | Detail Level |
|------|----------|-------------|
| Pending/overdue tasks | ✅ | id, title, due_date |
| Completed tasks (recent) | ✅ | id, title only |
| Old completed tasks | ✅ compressed | id, title, due_date |
| Note full content | ❌ | Never sent |
| Note titles (recent 5) | ✅ | id, title |
| Full chat history | ❌ | Only last 10 messages |
| Old chat summaries | ✅ | Summary text only |
| User profile data | ❌ | Never sent |

---

## 5. MEMORY SYSTEM

### Short-Term Memory
- Table: `ai_chat_messages`
- What: Full message content (role + content)
- When used: Last 10 messages injected into every context
- Retention: 7 days full detail

### Mid-Term Memory
- Table: `ai_chat_summaries`
- What: AI-generated summary of a completed session
- Trigger: When session has >20 messages OR session is >24h old
- Retention: 30 days

### Compressed Memory
- Table: `ai_memory_compressed`
- What: Items (tasks/notes) referenced in past chats
- Stored fields: `item_id`, `item_type`, `title`, `due_date` only
- Purpose: AI knows item exists and can link to it, without loading full content
- Retention: Indefinite (cleaned when item is deleted)

### Memory Flow
```
New chat message
    → saved to ai_chat_messages
    → items referenced? → saved to ai_memory_compressed

Session ends / gets old
    → summarize → save to ai_chat_summaries
    → delete raw messages older than 7 days
```

---

## 6. END-TO-END FLOW

### Flow A — Simple Query (No AI)
```
User: "Show my pending tasks"
→ AiChatController receives request
→ IntentDetectorService → DIRECT_QUERY
→ TaskRepository.findPendingByUser(userId)
→ Format as ChatResponseDto
→ Return to frontend
→ Frontend renders clickable task list
→ User clicks → navigates to /tasks/:id
```

### Flow B — Complex Query (AI Required)
```
User: "What should I focus on this week given my deadlines?"
→ AiChatController receives request
→ IntentDetectorService → AI_REQUIRED
→ ContextBuilderService builds context (tasks + notes titles + memory)
→ PromptBuilder assembles final prompt
→ OllamaService POST to localhost:11434
→ Ollama returns JSON response
→ Parse items array
→ Save to ai_chat_messages
→ Return ChatResponseDto to frontend
→ Frontend renders message + clickable items
→ User clicks item → /tasks/:id or /notes/:id
```

---

## 7. PRIORITY ORDER

### MVP (Build First)
1. `OllamaService` + basic connectivity test
2. `AiChatController` + `AiChatService` (no memory yet)
3. `ContextBuilderService` with just current tasks
4. Frontend `ai-chat` component + basic message UI
5. Clickable item rendering + routing

### Phase 2
6. `ai_chat_messages` table + save/retrieve history
7. `IntentDetectorService` (direct query bypass)
8. `ai_memory_compressed` table
9. Full context builder with memory

### Phase 3 (Optional/Advanced)
10. Session summarization scheduler
11. Note Assistant panel (in-note AI actions)
12. Semantic search with embeddings

---

## 8. RISKS TO AVOID

| Risk | Warning |
|------|---------|
| Overengineering memory | Start with short-term only. Add compression in Phase 2. |
| Sending too much to AI | HARD LIMIT: context must stay under 2000 tokens. Enforce in ContextBuilderService. |
| AI controlling business logic | AI formats and links only. All queries, filtering, auth = backend. |
| Ollama cold start latency | Warn user in UI ("AI is thinking...") — add loading state in frontend. |
| AI hallucinating task/note IDs | Validate all item IDs in AI response against DB before returning to frontend. |
| No fallback when Ollama is down | Add try/catch in OllamaService, return graceful error message in ChatResponseDto. |

---

## ✅ FINAL CHECKLIST BEFORE CODING

- [ ] Ollama is running locally: `ollama serve` + `ollama pull llama3`
- [ ] Flyway migrations added for 3 new tables
- [ ] `application.properties` has `ollama.base-url=http://localhost:11434`
- [ ] Angular route `/ai` added and protected by AuthGuard
- [ ] JWT interceptor already applies to `/api/ai/*` (no extra config needed)
- [ ] Prompt template reviewed and token count estimated before first test

---

*This document is the complete execution spec. Feed it to Cursor AI and generate phase by phase. Start with Phase 1 MVP only.*
