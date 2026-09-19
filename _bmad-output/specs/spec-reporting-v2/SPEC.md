# BMAD Feature Specification: Refactored Reporting Agent & Production Deployment (`spec-reporting-v2`)

**Version:** 2.0.0  
**Status:** Draft / Planned  
**Target:** Google Play Release & Cloud Run Deployment

---

## 🎯 1. Overview & Goals

This specification covers a complete refactoring of the **Reporting Engine Architecture**, the **On-Device & Cloud AI Selection**, **Offline Saved Reports Management**, and the **Production Cloud Run Backend Deployment**.

### Key Requirements
1. **Professional Problem-to-Resolution Reporting Schema**:
   - Initial status & problem observed
   - Work executed / resolution steps
   - Post-repair operational state
   - Price / Charge breakdown (Labor, Parts, Total)
   - Practical maintenance recommendations
2. **On-Device vs Cloud Agent Preference (Settings Screen)**:
   - Users can toggle between **On-Device Agent** (Local Engine / Gemini Nano AICore) and **Cloud Agent** (Gemini 2.5 Flash on Cloud Run).
3. **Offline-First Saved Reports List (`ReportListScreen`)**:
   - Reports are stored locally in Room DB first.
   - Users can browse saved jobs, view draft status, trigger final report generation, and share exported PDFs.
4. **Agentic Report Orchestration**:
   - The report generator acts as an agent taking job title, before/after photos, and text/voice description to synthesize a complete report.
5. **Production Backend Deployment**:
   - Fastify Node.js server containerized with Docker, deployed to Google Cloud Run with Secret Manager & HTTPS.

---

## 🏗️ 2. Architectural Design

### 2.1 Entity & Schema Updates (`ReportEntity.kt`)
Add price fields and problem-resolution structure to `ReportEntity`:
- `laborCost: Double?`
- `partsCost: Double?`
- `totalCost: Double?`
- `initialStatus: String?` (Problem observed)
- `resolutionStepsJson: String?` (Work completed)
- `currentOperationalState: String?` (Verified status)
- `aiAgentMode: String` (`ON_DEVICE` or `CLOUD`)

### 2.2 Navigation Graph Expansion (`MainActivity.kt`)
- `login` -> `report_list` (Saved Works Screen)
- `report_list` -> `capture` (New or Edit Job)
- `capture` -> `voice_capture`
- `capture` -> `review`
- `review` -> `report_ready`
- `settings` -> AI Agent Mode (On-Device vs Cloud), Business Name, Default Rates.

---

## 📄 3. PDF Generator Refactoring (`PdfReportGenerator.kt`)
Update PDF template layout:
- Header: Business Name, Customer Info, Job Title, Date.
- Section 1: Initial Status & Problem Observed.
- Section 2: Resolution Actions Taken.
- Section 3: Current Operational State.
- Section 4: Photo Grid (Before & After labeled images).
- Section 5: Price & Charge Breakdown (Parts, Labor, Total).
- Section 6: Recommendations & Maintenance.

---

## ☁️ 4. Production Cloud Run Backend Deployment
- **Dockerfile**: Production multi-stage build.
- **Secret Manager**: Secure `GEMINI_API_KEY` and Firebase Admin credentials.
- **Fastify Backend**: Endpoint `POST /v1/reports/generate` returning structured JSON matching problem, resolution, operational state, and estimated costs.
