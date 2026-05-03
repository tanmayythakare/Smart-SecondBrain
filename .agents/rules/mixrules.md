---
trigger: always_on
---

## Developer Rules (Execution Protocol)

Follow these consistently. No exceptions.

---

### 🔴 Debugging Rules

```id="dbg_rules"
1. Never restart twice for the same error
   → Read and understand before retrying

2. Always read the FULL error
   → Root cause is often above the final exception line

3. Isolate before fixing
   → Compare environments:
     - curl vs browser
     - sync vs streaming
     - backend vs frontend

4. One change per restart
   → Multiple changes = no clear cause
   → Debugging requires controlled variables

5. Never assume — always verify
   → "It should work" is invalid without proof
   → Confirm via logs, outputs, or prints
```

---

### 🟡 Building Rules

```id="build_rules"
6. If a bug repeats, eliminate the class of error
   → Add validation, guards, or constants
   → Do not rely on memory

7. Do not build on a broken state
   → Fix existing issues before adding features

8. Document intent, not action
   → Focus on "why", not "what changed"

9. Configuration must be externalized
   → No API keys, URLs, or model configs in code

10. curl works but browser fails
    → Check headers, CORS, and content-type first
```

---

### 🟢 Progress Rules

```id="progress_rules"
11. Verify every layer independently
    → Assumptions are not validation

12. Solve one problem at a time
    → Parallel bugs multiply confusion

13. If stuck for 20 minutes
    → Stop and explain:
       - Expected behavior
       - Actual behavior
    → This often reveals the gap (rubber duck method)

14. Maintain an error log
    → Track:
       - Issue
       - Attempted fixes
       - Final resolution

15. "Working" is contextual
    → curl ≠ browser ≠ production
    → Validate in the target environment
```

---

### 🔵 Mindset Rules

```id="mindset_rules"
16. Slow down to go faster
    → Careful analysis beats repeated retries

17. Treat errors as signals, not obstacles
    → They contain the diagnosis

18. "It worked before" is irrelevant
    → Identify what changed

19. Reduce complexity
    → If you can't explain it, you can't debug it

20. Prefer working over perfect
    → Ship → validate → improve
```

---

## Meta Rule (Non-Negotiable)

```id="meta_rule"
Fully understand the problem
before writing or changing code.
```

---

Everything else flows from this.