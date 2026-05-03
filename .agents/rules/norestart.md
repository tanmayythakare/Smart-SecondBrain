---
trigger: always_on
---

What’s Going Wrong
Something fails
→ restart
→ same failure
→ restart again
→ same failure
→ repeat loop

Restarting is not debugging. It doesn’t generate new information.

Core Rule

Never restart twice for the same error.

First failure  → read the error carefully
Second failure → indicates misunderstanding
               → STOP and diagnose before changing anything
Required Debugging Protocol

Follow this sequence every time something breaks:

1. READ
   → Review the full error message (not just the last line)

2. IDENTIFY
   → What exactly is failing?
     - App startup? → inspect logs from the beginning
     - API request? → check HTTP status + response
     - Streaming? → inspect browser Network tab

3. ISOLATE
   → Narrow it down:
     - Does curl/Postman work but browser fails?
     - Does sync work but streaming fails?
     - Is backend OK but frontend broken?

4. FIX
   → Apply a targeted change based on evidence

5. RESTART (once)
   → Only after a deliberate fix
What I Need From You Now

Provide:

1. The ONE specific issue that is failing
2. Exact error output (logs / console / network response)
3. What you’ve already tried

Focus on one problem at a time. No restart loops.