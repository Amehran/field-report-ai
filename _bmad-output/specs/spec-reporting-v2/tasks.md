# Implementation Tasks Checklist (`tasks.md`) — Refactored Reporting Agent & Production Deployment

**Document Version:** 2.0.0  
**Status:** In Progress / Planning  
**Output Location:** `_bmad-output/specs/spec-reporting-v2/tasks.md`

---

## 🚀 Phase 1: Database & Settings Foundation
- [ ] **Task 1.1: Database Schema Expansion**
  - Update `ReportEntity.kt` to include initial status, resolution steps, operational state, and price breakdown fields (`laborCost`, `partsCost`, `totalCost`).
  - Add Room migration / version update in `AppDatabase.kt`.
- [ ] **Task 1.2: App Settings & Agent Selection**
  - Implement `SettingsRepository` using Proto DataStore / SharedPreferences for AI Agent selection (`ON_DEVICE` vs `CLOUD`) and default labor rates.
  - Create `SettingsScreen.kt` UI with toggle switch and business customization settings.

---

## 📱 Phase 2: Saved Reports List UI & Offline-First Flow
- [ ] **Task 2.1: Saved Works / Reports List (`ReportListScreen.kt`)**
  - Create `ReportListScreen.kt` displaying all draft, pending, and approved reports saved locally in Room DB.
  - Add search bar, status filter chips, and floating action button to start a new job report.
  - Connect report list item click to edit job capture or view report PDF.
- [ ] **Task 2.2: Capture Screen Price & Cost Input Extensions**
  - Add optional Price / Cost input section (Parts, Labor, Total) to `CaptureScreen.kt`.
  - Ensure all report entries are saved locally to Room DB immediately before any AI request.

---

## 🤖 Phase 3: Agentic Report Generator & PDF Engine Refactor
- [ ] **Task 3.1: Dual Agent Orchestrator (`ReportAgentFactory.kt`)**
  - Implement `ReportAgent` abstraction with `CloudGeminiAgent` and `OnDeviceNanoAgent` (with local rule-based fallback).
  - Structure prompt schema into Initial Status, Resolution Executed, Current State, Price Summary, and Recommendations.
- [ ] **Task 3.2: PDF Generator Upgrade (`PdfReportGenerator.kt`)**
  - Redesign PDF document layout to render Initial Problem, Resolution, Current State, Price Breakdown table, and Before/After photo grid.

---

## ☁️ Phase 4: Cloud Run Backend Deployment & Play Store Readiness
- [ ] **Task 4.1: Fastify Backend Production Setup & Containerization**
  - Refine Gemini prompt in `backend/src/services/gemini.ts` for Problem -> Resolution -> Price JSON output schema.
  - Optimize `Dockerfile` for Google Cloud Run deployment.
- [ ] **Task 4.2: Android Production Build Variant & Verification**
  - Configure `build.gradle` buildConfig fields for production Cloud Run HTTPS URL vs local debug URL.
  - Run automated test suite (`./gradlew testDebugUnitTest`, `npm test`) and verify offline & cloud generation flows.
