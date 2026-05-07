# AIPT

AIPT is an Android AI personal trainer app built with Kotlin and Jetpack Compose. It helps users create a profile, generate workout plans, run guided workout sessions, track progress, and chat with an AI trainer.

## Features

- Profile and equipment setup
  - Basic profile, InBody/body composition metrics, training goals, preferences, and gym equipment availability.
  - View, edit, delete, and recreate the current profile.
  - Body metric snapshots for tracking body weight, body fat, and skeletal muscle mass over time.
- AI workout plan generation
  - Sends the user profile, body composition, goals, preferences, and equipment availability to the backend.
  - Stores confirmed workout schedules locally.
- Today dashboard
  - Shows the current training day, planned exercises, workout status, and session actions.
- Guided workout session
  - Logs weight, sets, reps, and notes per exercise.
  - Includes rest timer and session status tracking.
- Progress tracking
  - Weight progression chart by exercise.
  - Weekly volume chart using `sets * reps * weight`.
  - Body weight, body fat, and SMM trend chart.
  - AI-assisted day progress analysis and next-week day updates.
- Exercise library
  - Browse seeded exercises by muscle group and available equipment.
- AI trainer chat
  - Sends profile and workout context to the backend for training advice.

## Tech Stack

- Kotlin
- Jetpack Compose + Material 3
- Android Navigation Compose
- Hilt dependency injection
- Room database
- Retrofit + Gson
- Kotlin Coroutines + Flow
- Coil
- Vico dependencies are included for charting support

## Requirements

- Android Studio with Android Gradle Plugin support
- JDK 11 or newer
- Android SDK:
  - minSdk 26
  - targetSdk 36
  - compileSdk 36
- Backend API reachable from the Android device/emulator

## Backend API

The app currently points Retrofit to:

```kotlin
https://github.com/nminhcuongdev/workoutplannerAPI
```
Pull this repository and set up in your local machine.

Configured endpoints:

- `GET /health`
- `POST /api/v1/workout-plans`
- `POST /api/v1/workout-progress/analyze-day`
- `POST /api/v1/workout-progress/analyze`
- `POST /api/v1/workout-advice`

To change the backend address, update `NetworkModule`:

```text
app/src/main/java/com/example/aipt/di/NetworkModule.kt
```

The manifest enables internet access and cleartext traffic for local development.

## Getting Started

Clone the repository:

```powershell
git clone https://github.com/nminhcuongdev/AIPT.git
cd AIPT
```

Build the debug APK:

```powershell
.\gradlew.bat assembleDebug
```

Run tests:

```powershell
.\gradlew.bat test
```

Open in Android Studio:

1. Open the `AIPT` folder.
2. Let Gradle sync complete.
3. Start the backend API.
4. Run the `app` configuration on an emulator or Android device.

## Project Structure

```text
app/src/main/java/com/example/aipt
|-- core
|   |-- data/local
|   |-- navigation
|   |-- network
|   `-- ui/components
|-- di
|-- feature
|   |-- chat
|   |-- dashboard
|   |-- exercise
|   |-- home
|   |-- profile
|   `-- workout
`-- ui/theme
```

## Local Data

Room stores:

- User profile
- Gym equipment availability
- Workout days and schedules
- Workout session state
- Workout progress logs
- Body metric snapshots
- Seeded exercise library

The database uses destructive migration in development, so schema version changes can reset local data.

## Development Notes

- Main navigation is defined in `core/navigation/AppNavHost.kt`.
- Backend configuration is in `di/NetworkModule.kt`.
- Room database definition is in `core/data/local/AiptDatabase.kt`.
- The profile flow starts with `ProfileSetupScreen.kt` and `ProfileSetupViewModel.kt`.
- Progress charts and workout logging are in the workout feature package.
