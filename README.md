# Namma-Railu Buddy 🚂
### Passenger Guide for Local Trains — Android App

---

## Project Structure

```
NammaRailuBuddy/
├── app/src/main/java/com/nammarailu/app/
│   ├── MainActivity.kt              ← App entry point
│   ├── AppNavigation.kt             ← Screen routing
│   ├── data/
│   │   └── Models.kt                ← Station, Train, Coach, PlatformPing data
│   ├── viewmodel/
│   │   └── MainViewModel.kt         ← All app state & Firebase logic
│   ├── ui/
│   │   ├── theme/Theme.kt           ← Colors & MaterialTheme
│   │   └── screens/
│   │       ├── Components.kt        ← Shared UI components
│   │       ├── StationSelectionScreen.kt
│   │       ├── DashboardScreen.kt
│   │       ├── CoachLayoutScreen.kt
│   │       ├── PlatformPingScreen.kt
│   │       ├── AiAssistantScreen.kt
│   │       └── DestinationAlarmScreen.kt
│   ├── service/
│   │   ├── GeofenceManager.kt       ← GPS geofencing logic
│   │   ├── GeofenceBroadcastReceiver
│   │   └── AlarmService.kt          ← Notification + vibration alarm
│   └── util/Routes.kt               ← Navigation route constants
└── app/src/main/
    ├── AndroidManifest.xml
    └── res/values/
        ├── strings.xml
        └── themes.xml
```

---

## Setup Steps (Do These Before Running)

### Step 1 — Firebase Setup
1. Go to https://console.firebase.google.com
2. Create a new project → "NammaRailuBuddy"
3. Add an Android app → package name: `com.nammarailu.app`
4. Download `google-services.json`
5. Place it in `NammaRailuBuddy/app/` folder
6. In Firebase Console → Realtime Database → Create Database → Start in test mode

### Step 2 — Gemini AI Key (Free)
1. Go to https://aistudio.google.com
2. Click "Get API Key" → Create API key
3. Open `app/src/main/java/com/nammarailu/app/viewmodel/MainViewModel.kt`
4. Find this line: `val apiKey = "YOUR_GEMINI_API_KEY"`
5. Replace with your actual key

### Step 3 — Open in Android Studio
1. File → Open → select the `NammaRailuBuddy` folder
2. Wait for Gradle sync to finish
3. Plug in your Android phone (USB debugging ON) OR use an emulator
4. Press the green ▶ Run button

---

## Features Built

| Screen | Feature | Status |
|--------|---------|--------|
| Station Selection | Search + select station | ✅ Complete |
| Dashboard | Train list + alarm card | ✅ Complete |
| Coach Layout | Visual coach diagram | ✅ Complete |
| Platform Ping | Firebase crowdsourcing | ✅ Complete |
| AI Assistant | Gemini bilingual chat | ✅ Complete |
| Destination Alarm | GPS geofence 5km | ✅ Complete |

---

## Troubleshooting

**Gradle sync fails** → File → Invalidate Caches → Restart

**Firebase error** → Make sure `google-services.json` is in the `app/` folder

**Location not working** → Allow location permission when the app asks, or go to Settings → Apps → NammaRailuBuddy → Permissions → Location → Allow all the time

**AI not responding** → Add your Gemini API key in `MainViewModel.kt`

---

Built with ❤️ for Karnataka's local train passengers.
