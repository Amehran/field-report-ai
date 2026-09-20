# Google Play Store Publishing Checklist for Field Report AI

Follow this step-by-step checklist to publish **Field Report AI** to the Google Play Store via the Google Play Console.

---

## Pre-Release Preparation

- [x] **App Bundle (AAB) Generation**
  - Generated signed Android App Bundle (`app-release.aab`) with upload keystore.
  - Command: `./gradlew bundleRelease`

- [x] **Store Listing Metadata Prepared**
  - App Title (max 30 chars): `Field Report AI - Job Reports`
  - Short Description (max 80 chars): `Turn voice notes & site photos into professional customer PDF job reports in 3 mins.`
  - Full Description prepared in [`publish-assets/STORE_LISTING.md`](file:///Users/arminmehran/dev/field-report-ai/publish-assets/STORE_LISTING.md).

- [x] **Graphic Assets Prepared**
  - [x] High-Res App Icon (512x512 PNG/JPG): [`publish-assets/images/app_icon_512x512.jpg`](file:///Users/arminmehran/dev/field-report-ai/publish-assets/images/app_icon_512x512.jpg)
  - [x] Feature Graphic (1024x500 PNG/JPG): [`publish-assets/images/feature_graphic_1024x500.jpg`](file:///Users/arminmehran/dev/field-report-ai/publish-assets/images/feature_graphic_1024x500.jpg)
  - [x] Phone Screenshots (min 2, max 8):
    - [x] Screenshot 1 (Home Dashboard): [`publish-assets/images/screenshot_1_home.jpg`](file:///Users/arminmehran/dev/field-report-ai/publish-assets/images/screenshot_1_home.jpg)
    - [x] Screenshot 2 (Voice & Photo Capture): [`publish-assets/images/screenshot_2_voice.jpg`](file:///Users/arminmehran/dev/field-report-ai/publish-assets/images/screenshot_2_voice.jpg)
    - [x] Screenshot 3 (AI Structuring): [`publish-assets/images/screenshot_3_report.jpg`](file:///Users/arminmehran/dev/field-report-ai/publish-assets/images/screenshot_3_report.jpg)
    - [x] Screenshot 4 (PDF Export & Share): [`publish-assets/images/screenshot_4_pdf.jpg`](file:///Users/arminmehran/dev/field-report-ai/publish-assets/images/screenshot_4_pdf.jpg)

- [x] **Privacy Policy**
  - Privacy policy document created: [`publish-assets/PRIVACY_POLICY.md`](file:///Users/arminmehran/dev/field-report-ai/publish-assets/PRIVACY_POLICY.md).
  - Host policy at a public HTTPS URL (e.g., GitHub Pages or Firebase Hosting) and paste URL in Play Console under **App Content > Privacy Policy**.

---

## Google Play Console Step-by-Step Submission

1. **Create App Profile**
   - Log in to [Google Play Console](https://play.google.com/console).
   - Click **Create App**.
   - Set App name: `Field Report AI - Job Reports`.
   - Default language: `English (United States)`.
   - App or game: `App`.
   - Free or paid: `Free`.

2. **Set Up Main Store Listing**
   - Navigate to **Grow > Store presence > Main store listing**.
   - Copy Title, Short Description, and Full Description from [`STORE_LISTING.md`](file:///Users/arminmehran/dev/field-report-ai/publish-assets/STORE_LISTING.md).
   - Upload `app_icon_512x512.jpg` to **App icon**.
   - Upload `feature_graphic_1024x500.jpg` to **Feature graphic**.
   - Upload screenshots 1-4 under **Phone screenshots**.

3. **Complete App Content Questionnaire**
   - Navigate to **Policy > App content**.
   - Complete required sections:
     - **Privacy Policy**: Enter public URL.
     - **App Access**: Select "All functionality is available without restriction" (or provide test credentials for login).
     - **Ads**: Select "No, my app does not contain ads".
     - **Content Rating**: Complete IARC questionnaire (Productivity / Utility category).
     - **Target Audience**: Select 18+ (Business / Professional users).
     - **Data Safety Questionnaire**:
       - Microphone data collected for voice transcription (optional/user initiated).
       - Camera/Photos collected for report generation.
       - Personal Info (Email/Name) collected for account auth.

4. **Upload Release Bundle**
   - Navigate to **Release > Production** (or Internal Testing).
   - Click **Create new release**.
   - Upload `app-release.aab`.
   - Enter Release Notes: `Initial release of Field Report AI. Turn voice notes and photos into customer-ready PDF reports in minutes.`

5. **Review and Publish**
   - Click **Review release**.
   - Verify there are no critical errors.
   - Click **Start rollout to Production** to submit for Google review.
