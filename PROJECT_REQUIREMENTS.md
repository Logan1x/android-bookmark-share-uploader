# Android Bookmark Share Uploader — Project Requirements

## 1) One-line goal
Create an Android app that appears in the system **Share** sheet for links/text from any browser/app, receives the shared URL, and **uploads it to a configurable API endpoint** so bookmarks can be saved instantly without sending them to the assistant.

## 2) Primary user flow (MVP)
1. User is in Chrome/Brave/any app → taps **Share**.
2. Selects **Bookmark Uploader** (this app).
3. App extracts the URL (and optional title/selected text).
4. App sends a POST request to your API endpoint.
5. App shows a lightweight success/failure confirmation.

### MVP UX constraints
- Fast: no heavy UI, ideally a single “Uploading…” screen.
- Resilient: if offline, queue + retry.
- Safe: do not leak tokens; use HTTPS; show minimal error details.

## 3) Supported share inputs
### Must support
- `ACTION_SEND` with:
  - `EXTRA_TEXT` containing a URL (common browser behavior)
  - `text/plain`

### Should support
- `ACTION_SEND` with `text/plain` where EXTRA_TEXT contains additional text + URL
- `ACTION_SEND` from apps that provide:
  - `EXTRA_SUBJECT` as title

### Nice-to-have (later)
- `ACTION_SEND_MULTIPLE` (multiple links)
- `application/*` payloads if a browser shares a .url file (rare)

## 4) Data extracted & payload sent to API
### Fields to capture
- `url` (required)
- `title` (optional)
- `sourceApp` (package name; optional)
- `sharedText` (raw text; optional)
- `createdAt` (client timestamp; optional)

### Example request
`POST {API_BASE_URL}/api/bookmarks/import`

JSON body:
```json
{
  "url": "https://example.com",
  "title": "Optional title",
  "sharedText": "https://example.com ...",
  "sourceApp": "com.android.chrome",
  "createdAt": "2026-02-26T01:03:00+05:30"
}
```

## 5) Auth (important)
Pick one for MVP:

### Option A (simplest): Static API key
- App stores a single token and sends:
  - `Authorization: Bearer <TOKEN>`

**Storage:** Android Keystore + EncryptedSharedPreferences.

### Option B (better): Device-specific token
- API issues a token after a pairing step (QR or manual code)

For now, default to **Option A** unless you want pairing.

## 6) Offline + reliability
### MVP
- If upload fails, store a queued item locally and retry in background.

Recommended approach:
- Local persistence: Room DB (table `pending_uploads`)
- Background retries: WorkManager
- Constraints: network connected

## 7) App screens
### MVP screens
- **ShareReceiverActivity** (no launcher icon needed, but we can keep one for settings)
- **Status UI**: 
  - “Uploading…”
  - “Saved ✅” with close button
  - “Failed” with Retry

### Settings (minimal)
- API Base URL
- API Token
- Toggle: auto-close on success
- Debug: view last 20 uploads (optional)

## 8) Android technical requirements
- Language: Kotlin
- Min SDK: 26 (Android 8) unless you need lower
- Target SDK: latest stable
- Networking: OkHttp + Retrofit (or Ktor client)
- JSON: kotlinx.serialization or Moshi
- Dependency injection (optional): Hilt

## 9) Intent + manifest requirements
The app must register as a share target.

### Intent filters
- `android.intent.action.SEND`
- Categories: `DEFAULT`
- MIME types:
  - `text/plain`

Optional: `SEND_MULTIPLE`

## 10) API expectations (server-side)
Your API endpoint should:
- Validate URL
- Deduplicate (optional)
- Return JSON:
  - `{ "ok": true, "id": "..." }` or an error with message

## 11) Security & privacy
- Require HTTPS for production endpoints (warn on HTTP)
- Do not log tokens
- If logging URLs, provide a “disable logging” toggle
- Redact query params optionally (future)

## 12) Deliverables
### Phase 1 (MVP)
- Android project builds + installs
- Appears in Share sheet
- Extracts URL reliably
- Uploads to API + basic success/failure UI
- Queues failures + retries

### Phase 2
- Pairing flow
- Better parsing (title extraction via browser intent extras)
- Multiple links support
- Optional: Share-to-collection/tag selection

## 13) Open questions (answer these and we’ll lock scope)
1) What is your API endpoint URL/path and expected auth header?
2) Do you want this to work with **http://192.168.31.176** LAN endpoints too (self-hosted), or only public HTTPS?
3) Should the app auto-close immediately after success?
4) Do you want a launcher icon + settings screen, or truly “share-only”?
