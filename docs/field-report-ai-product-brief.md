# Field Report AI — Product Brief and Planning Requirements

**Status:** Planning baseline (MVP)  
**Working name:** Field Report AI  
**Product type:** Android-first B2B SaaS mobile application  
**Primary release goal:** Let a solo field-service professional create, approve, and send a polished customer job report from on-site photos and a voice note in under three minutes.

---

## 1. Executive summary

Field Report AI helps independent tradespeople document completed work without spending their evenings writing messages, organizing photos, or making PDFs. At the end of a job, the user captures before/after photos and records a short voice note. AI converts that raw material into a structured, customer-ready job report. The professional reviews it, makes any required changes, and shares it by PDF or a formatted message.

The product is **not** a full field-service management platform. It deliberately avoids scheduling, dispatch, payments, inventory, invoicing, CRM, and automated diagnosis in the MVP. The initial product wedge is faster, clearer job documentation.

### Core value proposition

> Take photos, speak what you did, and send a professional job report in minutes.

### Recommended first market

Start with **solo residential service contractors** and validate with one reachable trade only (for example: HVAC, electrical, restoration, painting, or handyman work). The app may be technically trade-neutral, but copy, report templates, and acquisition should focus on a single trade during launch.

---

## 2. Problem and opportunity

### Current user problem

After completing a job, a technician commonly sends a fragmented WhatsApp/SMS message, a few unlabelled photos, or nothing at all. Formal reports take too long, so they are skipped. This creates several problems:

- Customers are unclear about work completed, issues discovered, and recommended next steps.
- The contractor has weak evidence if a disagreement occurs.
- Before/after photos become difficult to find later.
- A small business looks less professional than it could.
- Follow-up work and customer trust are lost because recommendations are not communicated well.

### Product hypothesis

If a solo trade professional can turn photos and a spoken explanation into an accurate, branded report in under three minutes, they will send reports more consistently and will pay for the time saved and professionalism gained.

### Validation hypothesis

At least 3 of 5 early design partners should complete a real report, send it to a real customer, and state they would continue using the tool at a target price of approximately USD/CAD $19 per month.

---

## 3. Purpose, users, and jobs to be done

### Purpose

Make post-job communication and evidence capture effortless for small field-service businesses.

### Primary user: solo field-service professional

Examples: HVAC technician, electrician, plumber, painter, restoration contractor, handyman.

**Context**

- Works at a customer site, often with dirty hands, limited time, and inconsistent connectivity.
- Uses a phone as the primary tool for photos and customer messages.
- Is accountable for the report content and needs control before it is shared.

**Goals**

- Show the customer what was done.
- Record issues and recommendations clearly.
- Look professional without administrative work.
- Retain a job record for later reference or a dispute.

### Secondary user: customer/recipient

The homeowner, property manager, or business customer receiving the report.

**Goals**

- Understand the work, findings, and next actions without technical jargon.
- View relevant photos.
- Have a clear record to refer to later.

### Future users (out of MVP)

- Small-business owner or office administrator managing multiple technicians.
- Team technician who works under a company brand.

### Job-to-be-done statement

> When I finish work at a customer site, I want to quickly create a clear, professional summary with proof of what I did, so the customer understands the value and I have a record if questions arise.

---

## 4. Product scope

### MVP in scope

- Sign in and basic business profile (business name, logo optional, default contact details).
- Create a job with customer name, job title/address, and optional reference number.
- Capture/select photos and label them as before, after, or general.
- Record a voice note or enter typed notes.
- Transcribe voice and generate a structured report draft using AI.
- Edit every generated field before sharing.
- Create a branded PDF report.
- Share PDF or a formatted report summary using Android's native share sheet.
- Save job reports and access prior reports.
- Basic plan/usage limit support, even if payment is initially enabled only for testers.

### Explicitly out of scope for MVP

- Scheduling, calendar, dispatch, route planning, or employee tracking.
- Estimates, invoices, tax calculation, payments, and accounting integrations.
- Automated repair advice, regulatory compliance advice, or definitive fault diagnosis.
- Customer portal, customer accounts, signatures, or customer chat.
- Multi-user teams, role management, and enterprise administration.
- Integrations with Jobber, ServiceTitan, QuickBooks, WhatsApp Business API, etc.
- Video capture/analysis, live AI conversation, and hardware integrations.

---

## 5. User journey and workflow

### Primary workflow: complete and share a report

1. **Start job** — User taps “New report,” enters a customer/job name and optional address.
2. **Capture evidence** — User takes or selects photos. Each can be marked before, after, or general.
3. **Describe the work** — User records a 15–90 second note, or types notes when recording is inconvenient.
4. **Generate draft** — The application uploads selected media and notes securely, then requests a structured AI draft.
5. **Review and edit** — User sees an editable report; source photos and transcript remain accessible. The user corrects wording, removes details, and confirms accuracy.
6. **Approve** — User explicitly confirms that the report is accurate and suitable to send.
7. **Export/share** — The app creates a PDF and provides Android share-sheet options (email, SMS, WhatsApp, Drive, etc.).
8. **Retain record** — The completed report appears in job history and can be reopened, re-exported, or duplicated later.

### Failure and offline behavior

- Media and a local draft are saved immediately on-device.
- If connectivity is unavailable, the job remains in **Draft—waiting to generate** state.
- The app retries only with the user's clear knowledge and provides a visible “Generate when online” action.
- If AI generation fails, the user can retry or create/edit a report manually from the captured media and notes.

### First-use onboarding

1. Explain the one-sentence value proposition.
2. Capture business name and optional logo.
3. Select primary trade (used only for report vocabulary/template choice).
4. Show a 30-second guided example or sample report.
5. Start the first real report; do not require a long tutorial.

---

## 6. Inputs and outputs

### Inputs

| Input | Required | Source | Notes |
|---|---:|---|---|
| Job/customer name | Yes | Manual entry | Minimum identifier for report history. |
| Job address | No | Manual entry / future map picker | Treat as personal data. |
| Reference number | No | Manual entry | Useful for contractor workflows. |
| Photos | Recommended | CameraX camera or Android photo picker | Limit MVP to 10 images per report; compress before upload. |
| Photo type | No | User selection | Before, after, or general. |
| Voice note | One of voice/typed notes | In-app recorder | 15–90 seconds recommended; show recording duration. |
| Typed notes | One of voice/typed notes | Manual entry | Fallback and correction input. |
| Business profile | Yes, after onboarding | Stored profile | Name required; logo/contact optional. |

### AI-processing input

The backend receives the user-approved source inputs and a fixed prompt. It must request **structured JSON only**, not free-form prose. The prompt must instruct the model to:

- Use only facts supported by the transcript or provided user information.
- Never invent parts, diagnoses, test results, costs, warranties, permits, or guarantees.
- Mark uncertainty instead of guessing.
- Write clearly for a non-technical customer.
- Treat the text as a draft requiring user approval.

### Outputs

| Output | Recipient | Requirements |
|---|---|---|
| Editable report draft | Professional | Generated in seconds where connected; all text editable. |
| Voice transcript | Professional | Visible as a source, editable where supported. |
| Job-completion PDF | Customer and professional | Branded, readable on mobile, includes photos and date. |
| Shareable summary text | Customer | Short, polite, non-technical summary for share sheet. |
| Persisted job record | Professional | Searchable later by job/customer name and date. |

### Required report structure

1. Business identity and report date
2. Customer/job identity
3. Work completed
4. Findings / observations
5. Recommended next steps
6. Photo evidence with captions or labels
7. Disclaimer: “This report is a summary prepared by the service professional and should be reviewed for accuracy before relying on it.”

---

## 7. Functional requirements

### Must have (MVP / P0)

- **FR-01:** User can create, save, edit, and delete a draft job report.
- **FR-02:** User can attach up to 10 images from the camera or photo library.
- **FR-03:** User can record, play back, discard, and replace one voice note.
- **FR-04:** User can enter notes manually when they do not use voice.
- **FR-05:** System can transcribe voice and generate a draft from supported inputs.
- **FR-06:** System shows generation status and a useful retry message on failure.
- **FR-07:** User can edit all AI-produced fields before export; AI output is never automatically sent.
- **FR-08:** User must tap an explicit approval control before a report is marked complete or shared.
- **FR-09:** User can create a PDF containing approved report content and selected photos.
- **FR-10:** User can share a PDF and/or summary text through the Android share sheet.
- **FR-11:** User can view completed reports and reopen them to re-export.
- **FR-12:** Draft reports and captured media remain available after an app restart and while offline.

### Should have (P1, after core workflow is validated)

- Report templates tailored to one trade.
- Before/after photo-pair grouping.
- Customer/contact autocomplete from prior reports.
- Search and filters in history.
- Company logo and simple brand-color customization.
- Usage counter and RevenueCat/Google Play Billing subscription.
- Export report as an email-ready message body.

### Could have (P2 / later)

- Web dashboard and team accounts.
- Customer signatures and a customer portal.
- Integrations with field-service and accounting products.
- Follow-up reminders and “recommended work” tracking.
- Multi-language report output.
- Offline/on-device transcription and domain-specific models.

---

## 8. Non-functional requirements

### Performance and reliability

- Local photo capture must work without connectivity.
- Creating/saving a draft locally should feel immediate (target: under 300 ms for metadata).
- Online AI draft generation target: under 30 seconds for a typical report (up to 10 photos and a 90-second note), with an honest progress state.
- No user content may be lost if the app is backgrounded or closed while a draft is being prepared.
- Generated PDFs should open successfully using common Android PDF viewers.

### Security and privacy

- Do not embed LLM/API secrets in the Android app.
- Use authenticated backend requests and per-user access controls for all reports and media.
- Encrypt traffic in transit and use managed encryption at rest.
- Collect only data needed for report generation and storage.
- Show a clear privacy policy before production release, including what happens to audio, images, transcripts, and AI requests.
- Provide a user-controlled way to delete a report and its associated media.
- Do not use customer media or text for model training unless the user has separately opted in.

### Safety and trust

- Prominently label AI content as a **draft** until approved.
- Preserve user-entered/transcribed source text for review.
- Avoid wording that represents a professional diagnosis or legal/compliance determination unless the user wrote it.
- Use content moderation or rejection handling for abusive/unsafe inputs as required by the selected AI provider and app-store policy.

### Accessibility and usability

- Large tap targets for on-site use.
- Voice recording and key controls usable one-handed.
- Clear high-contrast status and error states.
- Report generation must never block the user from leaving the screen or continuing another task.

---

## 9. Recommended technical architecture

This stack favors a fast Android-first MVP while keeping AI credentials, usage control, and future portability off-device.

### Android application

- **Language/UI:** Kotlin, Jetpack Compose, Material 3.
- **Architecture:** Clean-ish modular architecture with MVVM; ViewModel + repository layers. Avoid premature multi-module complexity for MVP.
- **Navigation:** Navigation Compose.
- **Camera/media:** CameraX for capture; Android Photo Picker for existing images; Coil for image rendering.
- **Audio:** MediaRecorder / Media3 for voice capture and playback.
- **Local persistence:** Room for report metadata and work state; encrypted local storage only if sensitive tokens/data require it.
- **Background work:** WorkManager for resumable upload and generation retries.
- **PDF:** Generate a local PDF from approved content using Android PdfDocument or a well-supported PDF library. Keep the first layout simple and reliable.
- **Sharing:** FileProvider plus Android Sharesheet.
- **Analytics/crash reporting:** Firebase Analytics and Crashlytics, with privacy-conscious events and no raw customer report content in analytics.

### Backend and data

**Recommended fast-path:** Firebase + Cloud Run.

- **Authentication:** Firebase Authentication (email/password and Google sign-in).
- **Database:** Cloud Firestore for users, report metadata, and report lifecycle state.
- **Media storage:** Cloud Storage for Firebase with path-level, user-scoped rules.
- **AI orchestration API:** A small Cloud Run service (TypeScript/Node.js is a pragmatic choice) that authenticates the caller, enforces limits, invokes transcription and the selected LLM, validates JSON, and returns a report draft.
- **Secrets:** Google Secret Manager; never the Android client.
- **Observability:** Cloud Logging/Error Reporting plus redacted, structured generation metrics.

### AI services

- **Speech to text:** A managed speech-to-text API or selected multimodal AI provider.
- **Structured report generation:** A model/provider capable of reliable structured JSON output. The backend should enforce a JSON schema and reject/repair invalid output.
- **Image use in MVP:** Images are included in the final report. Use visual analysis only if it materially improves the report and if tested carefully; do not depend on image diagnosis for the initial release.

### Why this split

The mobile app owns the fast capture/review experience. The backend owns expensive AI calls, prompt/version control, rate limits, data access enforcement, and auditability. This is essential for cost control and for changing AI providers later.

---

## 10. Data model (planning level)

### User

- `id`, `email`, `displayName`
- `businessName`, `primaryTrade`, `logoUrl`, `contactDetails`
- `plan`, `usageCount`, `createdAt`

### Report

- `id`, `userId`, `status` (`draft`, `uploading`, `generating`, `needs_review`, `approved`, `failed`)
- `customerName`, `jobTitle`, `address`, `referenceNumber`
- `typedNotes`, `transcript`
- `workCompleted`, `findings`, `recommendations`, `customerSummary`
- `createdAt`, `approvedAt`, `updatedAt`
- `aiPromptVersion`, `generationModel`, `generationError`

### Media item

- `id`, `reportId`, `type` (`photo`, `audio`)
- `storagePath`, `localUri` (device only), `captureTime`
- `label` (`before`, `after`, `general`), `sortOrder`

### Export record

- `id`, `reportId`, `format`, `createdAt`, `sharedAt` (if the app can reliably observe it)

---

## 11. Success metrics

### North-star metric

**Approved reports shared per active professional per week.**

This measures whether the product is becoming part of real post-job work, rather than merely being installed or opened.

### MVP metrics

- Activation: percentage of sign-ups completing a first report within 24 hours.
- Time to value: median time from “New report” to PDF generated/shared.
- Completion rate: drafts that reach approved state.
- Share rate: approved reports that are exported/shared.
- AI edit rate: how often generated sections are materially changed; use this to improve prompts/templates.
- Week-2 retention: users who create a report in a second week.
- Paid intent: testers willing to pay at the planned price.

### Initial targets (hypotheses, not commitments)

- At least 60% of recruited testers create one report.
- Median first-report completion time under 5 minutes; repeat report under 3 minutes.
- At least 50% of approved reports are shared.
- At least 3 of 5 design partners request continued access or agree to a paid pilot.

---

## 12. Monetization and initial packaging

### Suggested launch offer

- **Free:** 3 reports to demonstrate value.
- **Solo:** USD/CAD $19/month for a reasonable monthly report limit (for example, 30 reports).
- **Later:** USD/CAD $39/month for unlimited reports, custom branding, and future customer-history features.

### Cost-control rule

Set an explicit report quota from day one. Generation should run only after the user asks to generate, never on every keystroke or photo upload. Log per-report AI cost, duration, and failure rate.

---

## 13. Key risks and mitigations

| Risk | Mitigation |
|---|---|
| AI invents a repair, diagnosis, or recommendation | Generate structured drafts only; state uncertainty; require user review/approval before export. |
| App becomes a generic, crowded field-service tool | Market to one trade and own the post-job documentation workflow first. |
| User does not trust AI with customer details/photos | Clear privacy policy, minimal data retention, explicit approval, and no AI keys in client. |
| On-site poor connectivity | Local draft persistence, queued generation, clear retry/manual fallback. |
| AI cost exceeds subscription value | Quotas, media limits, compression, server-side rate limiting, usage/cost telemetry. |
| Reports are not used often enough | Focus on a trade with frequent customer-facing job completion and photo evidence. |
| Customer expects report to be a legal/professional certification | Clear disclaimer and no claim that AI provides professional or compliance advice. |

---

## 14. Design-partner research plan

Recruit five professionals from one chosen trade. Observe, rather than only interview them.

### Questions

1. Show me the last job where you sent photos or an update to a customer.
2. What did you send, how long did it take, and what did you leave out?
3. When has a customer questioned work, scope, or a recommendation after the job?
4. At what point in the job would you use this app?
5. What wording or information must never be generated incorrectly?
6. Would you send the generated report to a real customer today after reviewing it?
7. What would make this worth $19/month—or make it not worth paying for?

### Evidence needed before expanding scope

- At least three real, customer-facing reports sent from the product.
- Clear agreement on the one report section users value most.
- Repeated feedback on one missing workflow, not a scattered list of feature requests.

---

## 15. MVP acceptance criteria

The MVP is ready for a closed pilot when a test user can:

1. Sign in and create a business profile.
2. Create a new report, capture at least two photos, and record a voice note.
3. Close/reopen the app without losing the incomplete report.
4. Generate a report draft online and see a useful error/retry state if generation fails.
5. Edit the work-completed, findings, and next-steps sections.
6. Explicitly approve the final text.
7. Export a readable PDF with photos and business identity.
8. Share the PDF with a standard installed Android app.
9. Reopen the completed report from history and export it again.
10. Use no embedded server/API secret and no unapproved automatic sharing.

---

## 16. Delivery sequence for spec-driven development

1. **Niche decision and design-partner interviews** — choose the first trade based on access and real workflow evidence.
2. **PRD refinement** — convert this brief into user stories, exact report schema, and non-goals.
3. **UX specification** — map the five core screens: onboarding, report list, new report, review/edit, share/history.
4. **Technical specification** — define API contract, JSON schema, Firestore rules, media constraints, and retry states.
5. **Build vertical slice** — new report → typed notes → editable report → PDF → share (no AI/voice initially if necessary).
6. **Add capture and AI** — photos, voice transcription, generation, review safeguards.
7. **Closed pilot** — five real professionals; observe their first real reports.
8. **Iterate only from observed behavior** — improve speed, vocabulary, and report trust before adding broad features.

---

## 17. Open decisions for the next planning phase

- Which exact first trade and geographic market will be targeted?
- Which AI provider meets target cost, structured-output quality, privacy terms, and latency?
- Will the first release include image analysis, or only include images in the PDF?
- What report template and disclaimer language does the selected trade need?
- Is a report quota or a time-limited trial better for first monetization testing?
- Which sharing destinations matter most for the first design partners: WhatsApp, SMS, email, or PDF file storage?
- What minimum retention/deletion policy is appropriate for customer photos and recordings?

---

## One-line product definition

**Field Report AI is an Android-first app that turns a field professional’s photos and voice note into an editable, customer-ready job-completion report.**
