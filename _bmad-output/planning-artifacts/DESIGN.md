# UX Design Specification (`DESIGN.md`) — Field Report AI

**Document Version:** 1.0.0  
**Target Platform:** Android (Jetpack Compose, Material 3)  
**Status:** Approved for Implementation  
**Output Location:** `_bmad-output/planning-artifacts/DESIGN.md`  
**Companion Document:** [`EXPERIENCE.md`](file:///Users/arminmehran/dev/field-report-ai/_bmad-output/planning-artifacts/EXPERIENCE.md)

---

## 1. Visual Identity & Design Philosophy

Field Report AI is built for **field reality**: bright outdoor sunlight, dirty or gloved fingers, rapid high-pressure job completion, and zero patience for fiddly micro-controls.

### Design Principles
1. **Clarity Over Flash:** High contrast, legible typography, and bold visual boundaries. Technicians glance at the screen under direct glare; text must be instantly readable.
2. **Utilitarian Elegance:** Professional, modern, and trust-inspiring. It looks like a high-end precision trade tool, not a playful toy or a bloated enterprise spreadsheet.
3. **Explicit State Signaling:** Amber warning badges for unreviewed AI drafts, clear emerald green for approved/ready states, and distinct pulsing ruby indicators during audio capture.

---

## 2. Color System & Semantic Tokens

The palette pairs an authoritative **Deep Teal / Emerald** brand accent with **Charcoal / Slate** structural surfaces and high-visibility status indicators.

```
Brand Primary:    #0D9488 (Teal 600) / #0F766E (Teal 700)
Dark Neutral:     #111827 (Gray 900)
Surface:          #FFFFFF (White)
Background:       #F8FAFC (Slate 50)
Border / Divider: #E2E8F0 (Slate 200)
```

### 2.1 Color Tokens Specification

| Token Name | Light Mode Hex | Purpose & Usage |
| :--- | :--- | :--- |
| `md_theme_light_primary` | `#0F766E` | Brand accent, active tabs, icon highlights, mic ring. |
| `md_theme_light_onPrimary` | `#FFFFFF` | Text/icons on primary color surfaces. |
| `md_theme_light_primaryContainer` | `#CCFBF1` | Subtle container background for selected items, trade badges. |
| `md_theme_light_onPrimaryContainer` | `#115E59` | Text/icons on primary container surfaces. |
| `md_theme_light_surface` | `#FFFFFF` | Elevated cards, bottom sheets, dialogs. |
| `md_theme_light_background` | `#F8FAFC` | App window background. |
| `md_theme_light_onSurface` | `#0F172A` | Primary typography and headings. |
| `md_theme_light_onSurfaceVariant` | `#64748B` | Secondary captions, timestamps, placeholder text. |
| `md_theme_light_outline` | `#CBD5E1` | Card outlines, input borders, dividers. |
| `md_theme_light_inverseSurface` | `#111827` | Primary CTA buttons ("Generate report", "Approve report", "Share PDF"). |
| `md_theme_light_inverseOnSurface`| `#F9FAFB` | Text on primary dark CTA buttons. |

### 2.2 Semantic & Status Tokens

| Semantic Role | Background Hex | Text / Foreground Hex | UI Context |
| :--- | :--- | :--- | :--- |
| **AI Draft Warning** | `#FEF3C7` (Amber 100) | `#92400E` (Amber 800) | `AI DRAFT — REVIEW REQUIRED` badge banner. |
| **Success / Approved** | `#DCFCE7` (Emerald 100) | `#15803D` (Emerald 700) | `Report Ready` status, checkmark pill. |
| **Recording / Alert** | `#FEE2E2` (Rose 100) | `#DC2626` (Rose 600) | Audio recording indicator, "Stop" button. |
| **Photo Tag (Before)** | `#F1F5F9` (Slate 100) | `#475569` (Slate 600) | Tag badge on "Before" photos. |
| **Photo Tag (After)** | `#E0F2FE` (Sky 100) | `#0369A1` (Sky 700) | Tag badge on "After" photos. |

---

## 3. Typography Hierarchy

Using Android system standard font (`Roboto` / `Inter` fallback) optimized for high legibility with generous letter-spacing on small screens.

| Style Role | Font Weight | Size (sp) | Line Height (sp) | Tracking | UI Usage |
| :--- | :--- | :---: | :---: | :---: | :--- |
| `displayLarge` | SemiBold (600) | 28 | 36 | -0.2 | Main onboarding / Ready title |
| `headlineMedium` | SemiBold (600) | 22 | 28 | 0.0 | Top app bar titles, Screen headings |
| `titleLarge` | Medium (500) | 18 | 24 | 0.0 | Card section titles ("Work completed") |
| `bodyLarge` | Regular (400) | 16 | 24 | 0.15 | Editable report text, body copy |
| `bodyMedium` | Regular (400) | 14 | 20 | 0.25 | Secondary descriptions, customer address |
| `labelLarge` | SemiBold (600) | 15 | 20 | 0.1 | Primary button text ("Generate report") |
| `labelMedium` | Medium (500) | 12 | 16 | 0.5 | Badges ("BEFORE", "AFTER", "AI DRAFT") |
| `timerDisplay` | SemiBold (600) | 32 | 40 | 1.0 | Audio recording timer (`00:28`) |

---

## 4. Spacing, Elevation & Layout Grid

### 4.1 Spacing Scale (8dp baseline)
* `space_xxs`: 2dp (micro dividers)
* `space_xs`: 4dp (badge insets, icon padding)
* `space_sm`: 8dp (spacing between badge and title)
* `space_md`: 16dp (standard screen margin, card inner padding)
* `space_lg`: 24dp (inter-card spacing)
* `space_xl`: 32dp (header to content separation)
* `space_xxl`: 48dp (top of screen / hero insets)

### 4.2 Elevation & Shapes
* **Corner Radius:**
  * App Cards: `16dp` rounded corners (`RoundedCornerShape(16.dp)`).
  * Primary Action Buttons: `12dp` rounded corners (`RoundedCornerShape(12.dp)`).
  * Tags / Pills: `100dp` fully rounded pill shape (`CircleShape`).
  * Photo Thumbnails: `12dp` rounded corners with subtle `1dp` outline.
* **Elevation:**
  * Cards: `1dp` resting elevation with `1dp` solid border (`#E2E8F0`) to maintain edge definition in outdoor glare.
  * Sticky Bottom Action Bar: `8dp` shadow elevation over scrolling content.

---

## 5. Screen-by-Screen Component Specifications

### 5.1 Screen 01: Login & Onboarding
* **Brand Mark:** Centered 72dp teal circle (`#0F766E`) with white "F" monogram.
* **Hero Copy:** Title `"Field Report AI"`, subtitle `"A professional report, before you leave the job."` in `Slate 600`.
* **Form Inputs:**
  * Clean outline text fields (`RoundedCornerShape(12.dp)`) for Email and Password.
  * Large 56dp height touch target.
* **Buttons:**
  * Primary Button: `"Continue"` (Solid Dark Charcoal `#111827`, White text).
  * Social Sign-in: `"Continue with Google"` with official multi-color "G" icon and subtle outline border.

### 5.2 Screen 02: New Report (Capture Hub)
* **Header:** Top Bar with `"New report"` title and close `"✕"` icon on right.
* **Job Identifier Card:**
  * Elevated card with customer name (e.g. `"Miller Residence"`), job subtext (`"Kitchen repair · Today"`), and right-aligned edit pill (`+` or pencil).
* **Photo Grid / Carousel:**
  * Horizontal scroll or 2x2 grid of thumbnails (96x96dp).
  * Overlay badges pinned top-left: `"BEFORE"` (`Slate 100`) or `"AFTER"` (`Sky 100`).
  * `"Add photo"` action button with camera icon in primary teal text.
* **Voice / Notes Card:**
  * Distinct elevated container with green microphone icon circle.
  * Headline `"Record a voice note"` and subtext `"or type notes instead"`.
* **Sticky Bottom CTA:**
  * Fixed at bottom with safe-area padding: 56dp tall `"Generate report"` button (`#111827`).

### 5.3 Screen 03: Voice Capture Screen
* **Top Navigation:** Back arrow (`<`) and screen title `"Describe the work"`.
* **Central Visualizer:**
  * Large 120dp circular microphone container (`#E6FFFA` background, `#0F766E` icon) with subtle pulsing outer ring animation.
  * Big high-contrast digital timer: `"00:28"` in 32sp SemiBold.
  * Sub-label: `"Listening..."` in `Slate 500`.
  * Animated audio waveform: 12-16 dynamic bars responding to live mic decibels in `#0F766E`.
* **Guidance Prompt:**
  * Center-aligned helper copy: *"Tell us what you completed and what the customer should know."*
* **Stop Action:**
  * Prominent 64dp red stop square button (`#DC2626`) in center bottom for effortless one-tap termination.

### 5.4 Screen 04: AI Review & Edit Screen
* **Warning Header:**
  * Prominent warning pill banner: `AI DRAFT — REVIEW REQUIRED` (`#FEF3C7` background, `#92400E` text, with alert icon).
* **Editable Section Cards:**
  * **Card 1: Work completed** (e.g. *"Replaced damaged cabinet hinge and realigned the kitchen cabinet door."*).
  * **Card 2: Findings** (e.g. *"Minor moisture marks were visible near the cabinet base."*).
  * **Card 3: Recommended next steps** (e.g. *"Monitor adjacent moisture and arrange an inspection if it returns."*).
  * Each card features an explicit `"Edit"` text action button in upper-right that opens an inline text field.
* **Approval CTA:**
  * Full-width sticky button: `"Approve report"` (`#111827`).

### 5.5 Screen 05: Report Ready & Share Screen
* **Status Confirmation:**
  * Title `"Report ready"` with emerald checkmark circle pill (`#DCFCE7` background, `#15803D` check icon).
* **Report Summary Card:**
  * Business Header: Dark slate card banner with business title (e.g. `"NORTHLINE HOME SERVICES"`), job subtitle, and current date.
  * Customer & Job details block.
  * Work completed snippet.
  * Thumbnail previews of attached photos.
  * `"VIEW FULL REPORT →"` text link.
* **Action CTAs:**
  * Primary: `"Share PDF"` (Full-width `#111827` button).
  * Secondary: `"Copy customer summary"` (Teal text action button with copy icon).

---

## 6. PDF Layout & Visual Template

The generated PDF must look pristine when printed or opened on mobile devices:

* **Page Size:** Standard US Letter / A4 portrait.
* **Margins:** 0.5 inch (36pt) margins all around.
* **Header Banner:**
  * Contractor Logo (left) and Business Contact Info (right).
  * Date and unique Reference Number.
* **Body Sections:**
  * Styled section divider rules with teal accent.
  * Section 1: Customer & Location Details.
  * Section 2: Work Completed (bullet list).
  * Section 3: Findings & Observations.
  * Section 4: Recommended Next Steps.
* **Photo Evidence Section:**
  * Fixed 2-column or 3-column grid with high-clarity aspect ratios.
  * Each photo labeled with badge overlay: `[Before]` or `[After]`.
* **Footer Disclaimer:**
  * Subdued 8pt font: *"This report is a summary prepared by the service professional and should be reviewed for accuracy before relying on it."*
