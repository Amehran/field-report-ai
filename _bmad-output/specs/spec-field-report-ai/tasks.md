# Implementation Tasks Checklist (`tasks.md`) — Field Report AI

**Document Version:** 1.0.0  
**Status:** Ready for Execution  
**Output Location:** `_bmad-output/specs/spec-field-report-ai/tasks.md`

---

## 🚀 Phase 1: Local Walking Skeleton (Zero Backend Dependency)
> **Goal:** Build the complete offline-first Android UI and native PDF export flow using mock data.

- [ ] **Task 1.1: Project & Theme Setup**
  - Scaffold Jetpack Compose project with Material 3.
  - Define color tokens (`#0F766E` Teal, `#111827` Charcoal, `#FEF3C7` Amber) and typography in `ui/theme/`.
- [ ] **Task 1.2: Local Database (Room)**
  - Implement `ReportEntity`, `MediaItemEntity`, Room DAOs, and Type Converters.
- [ ] **Task 1.3: Navigation & Capture UI (Screens 01 & 02)**
  - Implement `Screen01_Login` (Email/Google UI).
  - Implement `Screen02_Capture` (Customer input, photo grid with Before/After badges, typed notes fallback).
- [ ] **Task 1.4: Voice Capture Screen (Screen 03)**
  - Implement `Screen03_VoiceCapture` with MediaRecorder/Media3 audio capture.
  - Add timer (`MM:SS`), live decibel waveform animation, and 64dp red stop CTA.
  - Enforce `FLAG_KEEP_SCREEN_ON` wake-lock during recording.
- [ ] **Task 1.5: Native PDF Engine & ShareSheet (Screen 05)**
  - Build client-side `android.graphics.pdf.PdfDocument` generator.
  - Implement FileProvider and launch native Android ShareSheet (`Intent.ACTION_SEND`).

---

## ☁️ Phase 2: Firebase Foundation & Media Storage Sync
> **Goal:** Add authentication, Cloud Storage uploads, and WorkManager offline resilience.

- [ ] **Task 2.1: Firebase Authentication**
  - Integrate Firebase Auth SDK (Email/Password & Google Sign-In).
  - Persist user session and token refresh.
- [ ] **Task 2.2: Cloud Storage & Firestore Rules**
  - Configure Firebase Storage bucket and path-level security rules (`/users/{userId}/reports/{reportId}/*`).
  - Configure Firestore rules for user profiles and report metadata.
- [ ] **Task 2.3: WorkManager Resilient Sync**
  - Build `GenerateReportWorker` with `NetworkType.CONNECTED` constraint.
  - Implement WebP image compression (1600px max, 80% quality, <400KB) before upload.
  - Implement AAC audio compression (<500KB for 90s).

---

## 🤖 Phase 3: Cloud Run AI Gateway & Review Gate
> **Goal:** Deploy Cloud Run orchestrator and connect AI draft generation with human review.

- [ ] **Task 3.1: Cloud Run Fastify Service Scaffold**
  - Dockerize Node.js 20 / TypeScript Fastify gateway.
  - Implement Firebase Auth JWT verification middleware.
  - Connect Secret Manager for Gemini API key retrieval.
- [ ] **Task 3.2: Speech-to-Text & Gemini Flash Integration**
  - Implement Speech-to-Text adapter for `.m4a` audio files.
  - Implement Gemini Flash LLM prompt orchestrator with strict Zod JSON schema validation.
  - Implement `POST /v1/reports/generate` with `X-Idempotency-Key` header.
- [ ] **Task 3.3: AI Review & Approval Gate (Screen 04)**
  - Implement `Screen04_AIReview` with `AI DRAFT — REVIEW REQUIRED` amber banner.
  - Build inline text editing cards for `Work completed`, `Findings`, `Recommendations`.
  - Enforce explicit `"Approve report"` button requirement before unlocking PDF export.

---

## 🧪 Phase 4: Testing, Hardening & Closed Pilot
> **Goal:** Verify reliability under field conditions and test with 5 pilot contractors.

- [ ] **Task 4.1: Offline & Network Dropout Resilience Testing**
  - Verify app behavior when toggling Airplane Mode during capture, recording, and generation.
  - Confirm WorkManager auto-resumes uploads upon network restoration.
- [ ] **Task 4.2: Hardware & Ergonomic Polish**
  - Verify haptic vibration ticks on audio record start/stop/approve.
  - Audit high-contrast outdoor legibility and large tap targets.
- [ ] **Task 4.3: Closed Pilot Distribution**
  - Prepare Google Play Closed Testing track build.
  - Onboard 5 trade partners and observe sub-3-minute report completion.
