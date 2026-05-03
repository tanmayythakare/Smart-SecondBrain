# 🏰 Architectural Blueprint: The Perfected AI Orchestration Layer

This document defines the "Control Layer" philosophy for Life OS. It is designed to ensure the system is resilient, cost-efficient, and secure by treating the AI as a powerful but untrusted reasoning engine.

---

## 1. Control Layer (The Gatekeeper)
*   **Decision Engine**: Categorizes queries into *Deterministic* (handled by code), *Ambiguous* (handled by AI), or *Hybrid*.
*   **AI Call Policy**: Enforces strict rules on retries (max 2) and context sizes to prevent runaway costs.
*   **Context Budget Manager**: Measures tokens before sending and trims context dynamically to stay within safety windows.

## 2. Safety & Normalization
*   **Prompt Injection Guard**: Detects "ignore previous instruction" patterns. Wraps user input in data-only delimiters.
*   **Input Cleaner**: Normalizes casing and removes redundant junk tokens.
*   **Query Type Classifier**: Pre-determines if a query is *CRUD*, *Analytical*, or *Conversational*.

## 3. Intent System (Robustness)
*   **Hybrid Detection**: Logic-first (Regex/Keywords) + AI-fallback.
*   **Confidence Scoring**: If intent confidence is low, the system injects a broader "Safety Context."
*   **Failure Recovery**: If AI returns "I don't have access," the system triggers an automatic retry with an expanded data tier.

## 4. Context Intelligence
*   **Multi-Tier Context**:
    *   **Tier 1**: Recent/Priority items (always sent).
    *   **Tier 2**: Keyword-matched items.
    *   **Tier 3**: Semantic items (`pgvector`).
*   **Ranking Engine**: Scores items based on recency, priority, and similarity.
*   **Compression**: Summarizes long notes into snippets to save tokens.

## 5. Memory & Persistence
*   **Short-Term**: Last 3 exchanges (sliding window).
*   **Summarization**: Every 5 turns, compresses history into a "Context Summary."
*   **Long-Term**: Embeddings stored in PostgreSQL for semantic retrieval.

## 6. Execution & Validation
*   **Structured Prompts**: Strict JSON schemas and minimal conversational fluff.
*   **JSON Validator**: Fallback cleaning (stripping markdown) before parsing.
*   **Schema Enforcement**: Ensures required fields (message, items) exist.
*   **Output Sanitizer**: Verifies that any IDs mentioned by AI actually exist in the user's DB.

## 7. Recovery & Observability
*   **Multi-Step Retry**: Escalation logic from "Fix JSON" to "Expand Context" to "Useful Fallback."
*   **Full Pipeline Logging**: Tracks latency, token cost, and intent accuracy.
*   **Self-Improvement Loop**: Detects error patterns and adjusts routing rules automatically.

## 8. User Experience (UX) Intelligence
*   **Explainability**: Informs the user *why* specific context was used.
*   **Trust Mechanism**: Clickable navigation cards for verification.
*   **Feedback Capture**: 👍/👎 buttons to feed the Self-Improvement Loop.

---
> **Philosophy:** A perfect system doesn't just work when everything is right; it remains useful when the AI is wrong.
