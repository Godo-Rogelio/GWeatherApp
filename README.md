# GWeatherApp

A Jetpack Compose weather app for Android that displays current weather from the OpenWeatherMap API with location detection, user auth, and fetch history.

## Features

- **Login / Register** — simple auth backed by SharedPreferences
- **GPS Location Detection** — reverse-geocodes lat/lng to city name via `FusedLocationProviderClient` + `Geocoder`
- **Current Weather** — city name, temperature, sunrise/sunset times, and condition-based icons
- **Dynamic Day/Night UI** — background gradients and icon colors change based on sunrise/sunset times
- **Fetch History** — persist and browse past weather lookups with timestamps
- **Material 3 Design** — with dynamic color support (Android 12+)

## Tech Stack

| Layer | Libraries |
|---|---|
| UI | Jetpack Compose, Material 3, HorizontalPager, Material Icons Extended |
| Architecture | MVVM (`AndroidViewModel`, `StateFlow`, `ViewModelProvider.Factory`) |
| Networking | Retrofit 2 + Gson |
| Location | Google Play Services Location |
| Persistence | SharedPreferences (auth & weather history cache via Gson) |
| Testing | JUnit 4, Mockito, kotlinx-coroutines-test |

## Prerequisites

- Android Studio Ladybug (or later)
- JDK 17+
- An [OpenWeatherMap API key](https://openweathermap.org/api)

## Setup

1. Clone the repo
2. Open the project in Android Studio
3. Create a `local.properties` file in the project root (or edit the existing one):
   ```
   OPENWEATHER_API_KEY=your_api_key_here
   ```
4. Sync Gradle and run on a device/emulator (min SDK 26)

## API Key

The API key is loaded from `local.properties` at build time and exposed via `BuildConfig.OPENWEATHER_API_KEY`. It is **not** committed to version control.

## App Flow

1. **Auth** — user registers/logs in (credentials stored locally)
2. **Location** — coarse location permission requested; city detected via GPS or defaults to "Manila"
3. **Dashboard** — tabbed layout with:
   - *Current Weather* tab — fetches weather by city name, shows temperature, sunrise/sunset
   - *Fetch History* tab — shows previously fetched weather entries cached in SharedPreferences
4. **Restart** — history is restored from the local JSON cache on app launch

## Build

```bash
./gradlew assembleDebug
```

## Test

```bash
./gradlew test
```
