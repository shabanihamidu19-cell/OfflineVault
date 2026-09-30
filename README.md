# OfflineVault

**Private offline video vault for Android**

Paste a YouTube link → Extract streams → Download → Store in app-private storage → Play offline.

Videos **never** appear in Gallery or Files app.

> ⚠️ Educational / personal use only. This project uses unofficial extraction and violates YouTube Terms of Service. Do not distribute on Google Play.

---

## Features (Phase 1 - MVP)

- [x] Project setup with Clean Architecture
- [ ] Paste YouTube URL
- [ ] Extract title + available qualities (NewPipe Extractor)
- [ ] Download selected quality
- [ ] Save to app private storage (`filesDir`)
- [ ] Offline playback with Media3 (ExoPlayer)
- [ ] Basic library of downloaded videos

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
│   ├── extractor/
│   ├── downloader/
│   ├── local/
│   └── repository/
├── domain/
│   ├── model/
│   └── usecase/
├── presentation/
│   ├── home/
│   ├── library/
│   └── player/
└── util/
```

## Better Practices We Follow

- Clean Architecture (UI ↔ Domain ↔ Data)
- Single Responsibility
- Proper error handling
- Background downloads with WorkManager
- Private storage only
- Material 3 design
- Dependency Injection with Hilt

## Disclaimer

This project is for learning purposes. Downloading YouTube content without permission may violate copyright laws and YouTube's Terms of Service. Use responsibly.

---

**Owner:** KidCoder Tz  
**Status:** Active Development (Phase 1)
