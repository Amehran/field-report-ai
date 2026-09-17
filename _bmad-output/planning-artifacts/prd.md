# Product Requirements Document (PRD) — Field Report AI

**Document Version:** 1.0.0  
**Status:** Approved for Architecture & Planning  
**Target Release:** MVP (Phase 1 Closed Pilot)  
**Author:** Antigravity / BMad Method  
**Last Updated:** September 2026  
**Primary Output Artifact:** `_bmad-output/planning-artifacts/prd.md`

---

## 1. Executive Summary

### 1.1 Overview
**Field Report AI** is an Android-first mobile application that allows solo field-service professionals (HVAC technicians, electricians, plumbers, painters, restoration contractors, and handymen) to generate and share branded, customer-ready job completion reports in under three minutes using on-site photos and a spoken voice note.

### 1.2 Core Value Proposition
> *"Take photos, speak what you did, and send a professional job report in minutes."*

Technicians eliminate unbilled evening administrative work, while homeowners and property managers receive clear, documented proof of work completed, identified issues, and recommended next steps.

### 1.3 Target Launch Niche
* **Primary Target:** Solo residential service contractors.
* **Initial Launch Trade:** Single reachable trade (HVAC or residential electrical) to calibrate speech terminology, default disclaimers, and report templates.

---

## 2. User Personas & Jobs to Be Done (JTBD)

### 2.1 Personas

| Persona | Role | Primary Context & Pain Points | Primary Goals |
| :--- | :--- | :--- | :--- |
| **Frank (Solo Contractor)** | Primary User | Works on site with dirty/gloved hands, time pressure, and intermittent cell connectivity in basements/attics. Dislikes typing on mobile screens and hates spending evenings drafting emails or organizing photos. | Show customer proof of work, prevent scope disputes, look professional, and get paid faster without admin hassle. |
| **Sarah (Homeowner / Recipient)** | Secondary User | Non-technical customer receiving report via SMS, WhatsApp, or email. Often confused by technical contractor jargon. | Clear understanding of what was repaired, visual evidence (before/after), and recommended future maintenance. |

### 2.2 Job-to-be-Done (JTBD)
> **When I** finish physical work at a customer job site,  
> **I want to** quickly capture photo evidence and speak a brief explanation of what I did,  
> **So that** my customer receives a polished, professional report immediately and I have an indisputable record without losing my personal evenings to paperwork.

---

## 3. Scope & Boundary

### 3.1 In-Scope (MVP / Phase 1)
* **Authentication & Business Identity:** Email/password and Google sign-in; business name, contact info, and optional logo.
* **Job & Evidence Capture:** Job/customer name, address, up to 10 photos labeled (`before`, `after`, `general`), and 15–90s audio note (or typed fallback).
* **Offline-First Resilience:** Immediate local saving (Room DB); offline queueing (WorkManager); zero data loss on app close/restart.
* **Secure AI Orchestration:** Cloud Run proxy handling Speech-to-Text and Gemini Flash LLM structured output via strict JSON schemas.
* **Human-in-the-Loop Review Gate:** All generated fields editable; explicit "Approve" button required before PDF export; zero automatic sending.
* **Branded PDF Generation:** Local client-side generation using Android `PdfDocument` including company header, photo grids, and disclaimers.
* **Native Android Sharing:** Native share sheet integration (SMS, WhatsApp, Email, Drive, Print).
* **Job History:** Local list and details of past reports for re-opening or re-exporting.

### 3.2 Explicit Non-Goals (Out of Scope for MVP)
* No dispatch, scheduling, calendar, or GPS fleet routing.
* No estimates, invoicing, payments, accounting integrations (QuickBooks/Stripe), or CRM.
* No automated diagnostic tools, building code compliance determinations, or repair advice.
* No multi-user teams, employee permissions, or web admin portals (planned for v2).
* No customer portal or digital signatures in MVP.

---

## 4. User Journey & Core Screens Flow

Based on the validated 5-screen Android UI architecture:

```
[Screen 01: Login] 
       ⬇
[Screen 02: Job Capture] 
  - Customer & Job Title
  - Photo Gallery (Before / After / General tags)
  - Audio Record / Typed Notes
       ⬇
[Screen 03: Voice Capture]
  - 15-90s timer, audio waveform visualizer, stop/redo controls
       ⬇
[Screen 04: AI Review & Edit] ⚠️ AI DRAFT - REVIEW REQUIRED
  - Card 1: Work completed (Inline edit)
  - Card 2: Findings & Observations (Inline edit)
  - Card 3: Recommended next steps (Inline edit)
  - Action: [Approve Report] (Explicit user sign-off)
       ⬇
[Screen 05: Report Ready & Share]
  - Report preview card & photo thumbnails
  - Actions: [Share PDF] (Native Android ShareSheet) / [Copy Summary]
```

---

## 5. Functional Requirements (P0 Features)

### 5.1 Authentication & Business Profile
* **FR-01:** System must support user registration and sign-in via Firebase Authentication (Email/Password and Google Sign-In).
* **FR-02:** User can configure and edit a business profile containing: Business Name (required), Contact Phone/Email (optional), Primary Trade (dropdown), and Business Logo (optional image).

### 5.2 Job & Media Capture
* **FR-03:** User can create a new report specifying: Customer/Job Name (mandatory), Address (optional), Reference Number (optional).
* **FR-04:** User can capture photos directly via CameraX or select from the device gallery via Android Photo Picker (maximum 10 photos per report).
* **FR-05:** User can tag each photo as `before`, `after`, or `general`.
* **FR-06:** User can record an audio voice note (recommended duration 15–90 seconds) with play, discard, and re-record controls.
* **FR-07:** User can input typed notes as an alternative or supplement to voice recording.

### 5.3 Offline Storage & Background Sync
* **FR-08:** All job inputs (draft text, photo URIs, audio URI) must be immediately written to local Room database in under 300 ms.
* **FR-09:** If network connectivity is unavailable, the report status must transition to `draft_waiting_online`.
* **FR-10:** WorkManager must handle media uploads (compressed WebP images and AAC audio) when network connectivity is established, with automatic retry and exponential backoff.

### 5.4 AI Orchestration & Guardrails
* **FR-11:** Cloud Run orchestration API must validate user Firebase ID token, active quota, and request integrity before calling upstream AI services.
* **FR-12:** Audio notes must be transcribed into text; transcript must be preserved and accessible to the user.
* **FR-13:** LLM generation must produce strict structured JSON conforming to the defined schema:
  - `customerSummary`: 1-2 sentence non-technical overview.
  - `workCompleted`: Array of concise bullet points detailing completed tasks.
  - `findings`: Array of observed conditions or pre-existing defects.
  - `recommendations`: Array of suggested follow-up actions or preventative care.
* **FR-14:** Prompt Guardrails: The LLM must be explicitly forbidden from inventing unstated parts, repairs, warranties, prices, or regulatory/compliance certifications.
* **FR-15:** All AI output must be marked as `AI Draft — Review Required` until the user acts upon it.

### 5.5 Review, Edit & Approval Gate
* **FR-16:** The user must be able to tap and edit every text field generated by AI prior to report finalization.
* **FR-17:** Explicit Approval Gate: The application must require an explicit user tap on "Approve Report" before enabling PDF export or sharing. Automatic sharing without review is strictly prohibited.

### 5.6 PDF Generation & Sharing
* **FR-18:** System must generate a branded PDF document on-device using Android `PdfDocument` incorporating:
  - Header with business name, logo (if provided), report date, and reference ID.
  - Customer name and job location.
  - Work Completed, Findings, and Recommendations sections.
  - Organized photo grids with `Before` and `After` label badges.
  - Mandatory disclaimer: *"This report is a summary prepared by the service professional and should be reviewed for accuracy before relying on it."*
* **FR-19:** User can share the generated PDF and/or plain-text summary via native Android ShareSheet to apps like WhatsApp, SMS, Gmail, and Google Drive.

### 5.7 Report History
* **FR-20:** User can browse previously completed reports sorted by date, reopen any report, view attached photos, and re-share the generated PDF.

---

## 6. Non-Functional Requirements (NFRs)

### 6.1 Performance & Timing
* **Local Draft Write:** < 300 ms to persist draft state in Room.
* **AI Generation Round-Trip:** < 30 seconds for audio transcription + LLM structured extraction under normal 4G/5G connections.
* **End-to-End Task Completion:** Median time from "New Report" to "Shared PDF" under 3 minutes for experienced users.
* **Media Compression:** On-device image compression to WebP (< 400 KB per photo) and audio to AAC (< 500 KB per 90s clip) prior to upload.

### 6.2 Security & Trust Boundaries
* **Zero Client Secrets:** No AI provider API keys or master credentials may ever be embedded in the Android binary.
* **User-Scoped Security:** Cloud Storage and Cloud Firestore access must strictly enforce user boundary rules (`request.auth.uid == resource.data.userId`).
* **Data Deletion:** User has complete authority to delete any report, which cascades to delete all associated media in Cloud Storage and Firestore.
* **Privacy Assurance:** Customer photos and audio recordings are never used for model training without explicit opt-in.

### 6.3 Reliability & Offline Resilience
* App backgrounding, OS termination, or incoming phone calls must never corrupt or discard an ongoing draft.
* Cloud Run service must implement idempotent request tokens to prevent duplicate AI charges during mobile retry loops.

---

## 7. Data Models & JSON Schemas

### 7.1 Backend Structured Output Schema
```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "FieldReportAI_Draft",
  "type": "object",
  "required": ["customerSummary", "workCompleted", "findings", "recommendations"],
  "properties": {
    "customerSummary": {
      "type": "string",
      "description": "Polite, non-technical 1-2 sentence overview for the homeowner"
    },
    "workCompleted": {
      "type": "array",
      "items": { "type": "string" },
      "description": "Factual list of completed repairs or installations"
    },
    "findings": {
      "type": "array",
      "items": { "type": "string" },
      "description": "Observations, preexisting conditions, or diagnostic readings mentioned"
    },
    "recommendations": {
      "type": "array",
      "items": { "type": "string" },
      "description": "Suggested future service, filter replacements, or follow-up inspections"
    },
    "uncertainties": {
      "type": "array",
      "items": { "type": "string" },
      "description": "Muffled words or ambiguities flagged for contractor verification"
    }
  }
}
```

### 7.2 Firestore / Room Entity Relationship
* `User`: `userId`, `email`, `businessName`, `trade`, `logoUrl`, `planTier`, `reportsThisMonth`, `createdAt`
* `Report`: `reportId`, `userId`, `status`, `customerName`, `address`, `refNumber`, `transcript`, `structuredDraft`, `pdfLocalUri`, `createdAt`, `approvedAt`
* `MediaItem`: `mediaId`, `reportId`, `type` (`photo`|`audio`), `label` (`before`|`after`|`general`), `localUri`, `remoteStoragePath`, `sortOrder`

---

## 8. Success Metrics & Validation Targets

* **North Star Metric:** Approved reports shared per active professional per week.
* **Pilot Target 1 (Activation):** $\ge 60\%$ of recruited test users generate at least 1 real report in Week 1.
* **Pilot Target 2 (Speed):** Median report generation & approval time $\le 3\text{ minutes}$.
* **Pilot Target 3 (Share Rate):** $\ge 50\%$ of approved reports are shared with real clients.
* **Pilot Target 4 (Paid Intent):** $\ge 3$ of 5 design partners confirm willingness to pay $\$19/\text{month}$.

---

## 9. Next Steps in BMad Lifecycle
1. **`bmad-ux`**: Document design system tokens, Jetpack Compose UI component specs, and responsive phone layouts.
2. **`bmad-architecture`**: Formalize backend API contracts, Firestore rules, Room migrations, and Secret Manager configuration.
3. **`bmad-create-epics-and-stories`**: Deconstruct PRD into sprint-ready epics and user stories.
