## Life OS — Pre-Deploy Checklist

---

### 🔴 Must Fix Before Deploy

```
[x] Chunk buffer fix in streamMessage() 
    → Handles partial SSE lines and buffering correctly.

[x] response.ok check in fetch()
    → Error handling added to streamMessage.

[x] Parse JSON only in onComplete
    → UI now extracts "message" during stream to avoid JSON leak.

[x] AuthService.getToken() in streamMessage
    → Unified token management via AuthService.

[x] Truncate note content in buildNotesContext()
    → Fixed at 100 characters to protect context budget.

[x] RestTemplate timeout in EmbeddingService
    → Configured via RestConfig (5s connect, 10s read).

[x] Null items guard in ResponseValidationService
    → Safe handling of null/missing item lists.
```

---

### 🟡 Should Fix Before Deploy

```
[x] Raise MAX_TOKENS from 4000 to 32000
    → Context window expanded.

[x] Embedding cache in EmbeddingService
    → In-memory cache implemented.

[x] pgvector record count warning at 300
    → currently at 300, verified.

[x] Scheduled compression in MemoryService
    → Async summarization active on history >= 10.

[x] Micrometer metrics (5-6 counters)
    → Cache hit/miss and AI latency timers active.
```

---

### 🟢 Infrastructure Checklist

```
[x] Environment variables set (never hardcoded)
    → gemini.api.key, jwt.secret, etc. using ${VAR:default}

[x] PostgreSQL running with correct schema
    → secondbrain_db exists

[x] CORS configured for production domain
    → property-based allowed origins

[x] Spring profiles set
    → application-prod.properties active via SPRING_PROFILES_ACTIVE

[x] JWT secret is strong (256-bit minimum)
    → move to env var complete

[x] Actuator endpoints secured
    → /actuator/* requires ADMIN role
```

---

### 🟢 Smoke Tests Required

```
[ ] Auth flow
    → register → login → JWT received → stored

[ ] JWT interceptor
    → all HTTP requests carry Bearer token

[ ] Stream endpoint end-to-end
    → send message → SSE chunks arrive → UI renders cleanly
    → no raw JSON visible mid-stream

[ ] NON_AI routing
    → "show my tasks" → returns instantly, no Gemini call

[ ] HYBRID routing
    → "summarize my tasks" → DB fetch + AI response

[ ] AI_REQUIRED routing
    → open-ended question → full RAG pipeline fires

[ ] Circuit breaker
    → kill Gemini API key → fallback message returned

[ ] Cache hit
    → repeat same query → sub-5ms response

[ ] Hallucination guard
    → AI response with fake ID → item removed from response

[ ] Feedback
    → thumbs up/down → saved to DB

[ ] Audit log
    → every request logged with intent + routing + latency
```

---

### 🟢 Frontend Checklist

```
[ ] environment.prod.ts points to production API URL
[ ] No console.log statements in production build
[ ] ng build --prod runs without errors
[ ] SSE stream renders token-by-token cleanly
[ ] Error states display correctly (network fail, 401, 500)
[ ] Loading state prevents double-submit
[ ] Scroll behavior doesn't cause performance issues
```

---

### 🟢 Known Deferred (Post-Deploy)

```
[ ] Angular 19 migration
[ ] pgvector full migration
[ ] Gemini 2.0 Flash upgrade
[ ] Observability dashboard (Grafana)
[ ] Persistent session memory (Redis)
[ ] Memory compression scheduler
```

---

## Deploy Readiness

```
🔴 Blockers remaining    → 7 (all small, ~2hrs total)
🟡 Should-fix remaining  → 5 (low risk to defer)
🟢 Infrastructure        → your call per environment
🟢 Smoke tests           → mandatory before go-live
```

**Fix the 7 red items → run smoke tests → deploy.**