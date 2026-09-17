# Unified Technical Specification (`SPEC.md`) — Field Report AI

**Document Version:** 1.0.0  
**Status:** Approved for Build  
**Output Location:** `_bmad-output/specs/spec-field-report-ai/SPEC.md`  
**Prerequisite Artifacts:**
- [`prd.md`](file:///Users/arminmehran/dev/field-report-ai/_bmad-output/planning-artifacts/prd.md)
- [`DESIGN.md`](file:///Users/arminmehran/dev/field-report-ai/_bmad-output/planning-artifacts/DESIGN.md)
- [`EXPERIENCE.md`](file:///Users/arminmehran/dev/field-report-ai/_bmad-output/planning-artifacts/EXPERIENCE.md)
- [`architecture.md`](file:///Users/arminmehran/dev/field-report-ai/_bmad-output/planning-artifacts/architecture.md)

---

## 1. Product Summary & Goal
**Field Report AI** is an Android B2B SaaS application enabling solo trade professionals (HVAC, electrical, plumbing, handyman) to capture on-site photos and a voice note, generate an AI-assisted structured report draft, edit & approve it, and share a branded PDF with their customer in **under 3 minutes**.

---

## 2. System Architecture & Tech Stack

```
[ Android Client ] ──(Firebase Auth JWT)──> [ Cloud Run Orchestrator ]
  │ Kotlin 2.0 / Compose                    │ Node.js 20 / Fastify
  │ Room DB (Offline Drafts)                │ Zod Schema Guardrails
  │ CameraX + Media3 Audio                  │ Speech-to-Text Adapter
  │ Native PdfDocument                      │ Gemini Flash LLM
  └──(Storage Upload)──> [ Cloud Storage ] <┘
```

* **Android App:** Kotlin, Jetpack Compose (Material 3), MVVM, Coroutines + Flow, Room DB, WorkManager, CameraX, Android Photo Picker, Media3/MediaRecorder, native `PdfDocument`, Android `ShareSheet`.
* **Backend Gateway:** Dockerized Cloud Run (Node.js 20 LTS / Fastify / TypeScript).
* **Firebase Services:** Firebase Auth (Email/Password + Google), Cloud Firestore, Cloud Storage for Firebase.
* **AI Provider:** Speech-to-Text API + Google Gemini 1.5/2.0 Flash (structured JSON with Zod validation).
* **Secrets:** Google Secret Manager (zero AI keys in Android client).

---

## 3. Core Workflow & Human-in-the-Loop Safeguards

1. **Capture:** User enters customer name, captures up to 10 photos (`Before`/`After` tags), records 15–90s audio note (or types fallback notes).
2. **Offline Local Save:** Instant Room DB write (<300ms).
3. **Async Generation:** WorkManager compresses media (WebP/AAC) ➔ uploads to Cloud Storage ➔ calls Cloud Run `/v1/reports/generate`.
4. **Structured Output:** Cloud Run transcribes audio ➔ calls Gemini Flash ➔ validates JSON ➔ returns draft (`customerSummary`, `workCompleted`, `findings`, `recommendations`, `uncertainties`).
5. **Review & Approval Gate:** User reviews editable draft cards with `AI DRAFT — REVIEW REQUIRED` banner. User taps `"Approve report"` (explicit sign-off required).
6. **Local PDF & Share:** Android native `PdfDocument` generates PDF locally ➔ dispatches via native Android ShareSheet (WhatsApp, SMS, Email, Drive).

---

## 4. Primary Data Models

### 4.1 Room / Firestore Report Schema
* `id`: String (UUID)
* `userId`: String (Firebase UID)
* `status`: `DRAFT` | `WAITING_ONLINE` | `GENERATING` | `NEEDS_REVIEW` | `APPROVED` | `COMPLETED`
* `customerName`: String
* `jobTitle`: String
* `address`: String?
* `referenceNumber`: String?
* `typedNotes`: String?
* `audioLocalUri`: String?
* `audioStoragePath`: String?
* `rawTranscript`: String?
* `customerSummary`: String?
* `workCompletedJson`: String? (Serialized List<String>)
* `findingsJson`: String? (Serialized List<String>)
* `recommendationsJson`: String? (Serialized List<String>)
* `pdfLocalPath`: String?
* `createdAt`: Long
* `approvedAt`: Long?

---

## 5. Definition of Done (DoD) for MVP
- [ ] User can sign in, create profile, and capture job details + photos + voice note offline.
- [ ] WorkManager uploads media and triggers Cloud Run generation when network is available.
- [ ] Cloud Run returns grounded JSON draft without inventing unstated repairs or warranties.
- [ ] Android app displays editable cards and blocks PDF generation until explicit "Approve" tap.
- [ ] Client generates branded PDF on-device and opens Android ShareSheet.
