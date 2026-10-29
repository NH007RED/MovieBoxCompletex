# MovieBox v16.0 — Movie & Web Series Streaming Catalog with Admin Panel

## What's new in v16
- **Loading (splash) screen redesigned**: staggered poster-wall fade-in with parallax drift, breathing crimson glow, gradient progress bar with moving shimmer, live percentage and rotating status messages. All previous splash elements and the poster wall are kept.
- **Details page (movies & web series)**: Share icon removed (top-right button, "Share Title" menu item and the Full Information page share button).
- **Complete full-screen UI**: transparent status bar + transparent navigation bar, display-cutout support (`values-v28`), details hero now draws behind battery / network / time icons.
- **Admin -> User Management bug fixed**: users were never written to `/users`, and the admin `/users` listener was attached before login (rejected by rules and cancelled forever). Now every device registers itself (`UserPresenceManager`) and the admin listener attaches only after the admin is verified. One entry per device, live Online / Offline.

### One-time Firebase setup required for User Management
Firebase Console -> **Authentication -> Sign-in method -> Anonymous -> Enable**.
(Realtime Database rules are unchanged; `/users/{uid}` is already writable by its own uid.)


MovieBox is a native Android application built with **Kotlin**, **Jetpack Compose (Material 3)**, **Firebase Realtime Database**, **Firebase Authentication**, **Retrofit/OkHttp (TMDB API v3)**, **Media3 ExoPlayer**, **Coil**, and **Jetpack DataStore**.

---

## Pre-Configured Project Settings
- **Firebase Project (`app/google-services.json`)**: Pre-loaded with your `nh-movie-13248` (`https://nh-movie-13248-default-rtdb.firebaseio.com`) configuration for package `com.moviebox.app`.
- **TMDB v3 API Key (`app/build.gradle`)**: Pre-configured with `2e211dfda888f7cc55ce433d743f9bc3` (and can also be updated at runtime in **Admin Panel -> Settings / Import TMDB**).
- **Build Stack**: Gradle 8.9, Android Gradle Plugin 8.7.3, Kotlin 2.0.21, `compileSdk 35`, `minSdk 24`, `targetSdk 35`, JDK 17.
