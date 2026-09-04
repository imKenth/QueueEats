# QueueEats

QueueEats is a modern, responsive Android application designed to streamline the dining experience. The app features a high-fidelity user interface with smooth transitions, immersive full-screen display, and a focus on clean, reusable architectural patterns.

## Key Features

- **Immersive Splash Screen**: A beautifully designed entry point with custom decorative elements and automatic navigation.
- **Responsive Design**: Fully adaptive layout built with ConstraintLayout that works seamlessly across various screen sizes (small phones, tablets, and landscape orientation).
- **Reusable Branding Component**: Implements an <include> based header system for consistent branding across Sign In and Sign Up screens.
- **Edge-to-Edge Experience**: Utilizes modern Android APIs to draw content behind system bars for a truly full-screen feel.
- **Immersive Mode**: Optional status bar and navigation bar hiding to maximize screen real estate.
- **Smooth Navigation**: Intent-based transitions between Authentication screens.

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Android XML (View System)
- **Layouts**: ConstraintLayout, ScrollView (for small screen compatibility)
- **Design System**: Material Design 3 (M3)
- **Minimum SDK**: 26 (Android 8.0)
- **Target SDK**: 35 (Android 15)

## Project Structure

```text
app/src/main/
├── java/com/example/queueeats/
│   ├── splashScreen.kt    # Entry activity with timer logic
│   ├── MainActivity.kt    # Sign In screen logic
│   └── SignupActivity.kt  # Sign Up screen logic
└── res/layout/
    ├── splashscreen.xml            # Splash UI
    ├── signinscreen.xml            # Responsive Sign In UI
    ├── signupscreen.xml            # Responsive Sign Up UI
    └── layout_header_branding.xml  # Reusable branding header
```

## Getting Started

### Prerequisites
- Android Studio Ladybug (or newer)
- Android SDK 35
- Java 11 or higher

### Installation
1. Clone the repository.
2. Open the project in Android Studio.
3. Sync Project with Gradle Files.
4. Run on an emulator or physical device.

## Design Notes

The application uses a "Blur & Wave" aesthetic:
- **Header**: Features a high-quality blurred restaurant background.
- **Waves**: Smooth vector-based wave transitions (rectangle1, rectangle2) between branding and form areas.
- **Typography**: Custom fonts (Poppins, Kedebideri, Konkhmer Sleokchher) for a premium look.

## Developer

**Kenth Daryl Velasco**  
*Main UI/UX and Android Developer*

---
*Developed as part of the QueueEats Project.*
