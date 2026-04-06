<div align="center">
  <h1>📦 SubTrack</h1>
  <p>A modern Android app to track and manage all your subscriptions in one place.</p>

  ![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
  ![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
  ![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
  ![Min SDK](https://img.shields.io/badge/Min%20SDK-24-brightgreen?style=for-the-badge)
  ![Target SDK](https://img.shields.io/badge/Target%20SDK-36-blue?style=for-the-badge)
  ![Version](https://img.shields.io/badge/Version-1.0-orange?style=for-the-badge)
</div>

---

## 📱 About

SubTrack helps you stay on top of all your subscriptions — from streaming services to SaaS tools. Never miss a renewal or overspend on forgotten services again. With a clean Material 3 UI, real-time backend sync, and offline-first support, SubTrack makes subscription management effortless.

---

## ✨ Features

- 📋 **Subscription Dashboard** — View all active subscriptions at a glance
- 🔔 **Renewal Reminders** — Get notified before any subscription renews
- 💸 **Spending Overview** — Track monthly and yearly subscription costs
- 🔄 **Backend Sync** — Subscriptions synced via REST API with offline-first support
- 🔐 **Secure Storage** — Credentials stored using AndroidX Security Crypto
- 📄 **Paginated List** — Smooth scrolling with Paging 3 + Room Paging
- 🌐 **Image Loading** — Subscription logos loaded via Coil
- 💾 **Local Persistence** — Offline support powered by Room database

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| Dependency Injection | Hilt |
| Local Database | Room + Room Paging |
| Networking | Retrofit + Gson |
| Image Loading | Coil |
| Preferences | DataStore Preferences |
| Pagination | Paging 3 |
| Secure Storage | AndroidX Security Crypto |
| Navigation | Navigation Compose |

---

## 🏗️ Project Structure

```
app/
└── src/
    └── main/
        └── java/com/selvaganesh7378/subtrack/
            ├── data/          # Repository, API services, Room DB
            ├── domain/        # Use cases, models, interfaces
            ├── presentation/  # Compose screens, ViewModels
            └── di/            # Hilt modules
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Ladybug or later
- JDK 11+
- Android device/emulator with API 24+

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/SELVAGANESH7378/SubTrack.git
   cd SubTrack
   git checkout develop
   ```

2. **Open in Android Studio**
   Open the project folder in Android Studio and wait for the Gradle sync to complete.

3. **Configure API Base URL**
   Set your backend base URL in the Retrofit builder or via `local.properties`:
   ```
   BASE_URL=https://your-api-url.com/
   ```

4. **Run the app**
   Select a device/emulator and click ▶️ **Run**.

---

## 🌿 Branch Strategy

| Branch | Purpose |
|---|---|
| `main` | Stable, production-ready code |
| `develop` | Active development branch |
| `feature/*` | Individual feature branches |

---

## 🤝 Contributing

1. Fork the repo and create a feature branch from `develop`
2. Make your changes and write tests where applicable
3. Open a Pull Request to `develop` with a clear description

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

<div align="center">
  Made with ❤️ by <a href="https://github.com/SELVAGANESH7378">SELVAGANESH7378</a>
</div>
