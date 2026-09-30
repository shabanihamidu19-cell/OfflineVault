# OfflineVault

**Private offline video vault for Android**

Paste a YouTube link → Extract streams → Download → Store in app-private storage → Play offline.

Videos **never** appear in Gallery or Files app.

> ⚠️ Educational / personal use only. This project uses unofficial extraction and violates YouTube Terms of Service. Do not distribute on Google Play.

---

## Features (Phase 1 - MVP) ✅

- [x] Project setup with Clean Architecture
- [x] Paste YouTube URL
- [x] Extract title + available qualities (NewPipe Extractor)
- [x] Download selected quality (foreground + progress)
- [x] Background download with WorkManager + notification
- [x] Save to app private storage (`filesDir/videos/`)
- [x] Offline playback with Media3 (ExoPlayer)
- [x] Library screen (list / play / delete)
- [x] Bottom navigation (Download + Library)

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt |
| Async | Coroutines + Flow |
| Extractor | NewPipe Extractor |
| Downloader | OkHttp + WorkManager |
| Player | Media3 (ExoPlayer) |
| Storage | App Internal Storage |

## Project Structure

```
com.offlinevault
├── di/
├── data/
│   ├── extractor/     # NewPipe wrapper + HTTP downloader
│   ├── downloader/    # WorkManager worker + scheduler
│   ├── local/         # PrivateStorageHelper
│   └── repository/
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
├── presentation/
│   ├── home/
│   ├── library/
│   ├── player/
│   ├── navigation/
│   └── theme/
└── OfflineVaultApp.kt
```

## How it works

1. User pastes YouTube URL
2. NewPipe Extractor fetches streams
3. User picks quality
4. Download runs (UI progress **or** background WorkManager)
5. File saved under `context.filesDir/videos/` — Gallery cannot see it
6. Library lists files; Player plays with ExoPlayer

## Better Practices

- Clean Architecture (UI ↔ Domain ↔ Data)
- Single Responsibility
- Hilt Dependency Injection
- WorkManager for reliable background work
- Private storage only
- Material 3 + Compose

## Build

Open in Android Studio (Ladybug+), sync Gradle, run on device/emulator (API 26+).

You still need default launcher icons (`mipmap`) — Android Studio can generate them.

## Disclaimer

Learning project only. Unofficial YouTube extraction may break and may violate ToS / copyright. Use responsibly.

---

**Owner:** KidCoder Tz  
**Status:** Phase 1 MVP complete — polish & test next
