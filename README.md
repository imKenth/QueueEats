# QueueEats: Canteen Ordering & Queue Management System

QueueEats is a robust Android application built with Kotlin and XML in Android Studio designed specifically for campus canteens and pre-cooked food stalls. It streamlines pre-ordering, eliminates long lines with automated branch queue numbers, provides live queue tracking, and empowers canteen staff with inventory stock control.

---

## Key Features

### 1. Customer Ordering & Menu
- **Branch Menu & Stock Availability:** Browse available food items per branch with real-time stock badges (*"SOLD OUT"*, *"Only X left!"*, *"Stock: 15"*).
- **Open Food Facts API Integration:** Tap any menu item to instantly query the 100% free Open Food Facts database (with local traditional Filipino meal fallback) to view nutritional facts, calories, and ingredients.
- **Shopping Cart & Checkout:** Adjust quantities, view subtotals and totals in Philippine Pesos (PHP), and confirm orders with single-tap duplicate protection.
- **Interactive Branch Maps:** Open interactive OpenStreetMap (OSMDroid) views centered on each branch's exact coordinates (*Janpol Ecoland Stall Orange* & *Janpol Calinan stall red*).

### 2. Automated Queue Management
- **Independent Branch Queues:** Automatically generates unique sequential queue numbers per branch (e.g., `A001...` for Branch 1, `B001...` for Branch 2).
- **Order Confirmation & Live Queue:** Prominent queue number display (e.g., **A012**), itemized receipt summary, and live status tracking (*"Now Calling"*, customer status, and orders ahead in line).
- **Customer Order History:** View past and active orders from the Profile screen ("My Orders").

### 3. Canteen Staff Portal & Inventory
- **Secret 3-Tap Login:** Tap the app logo on the Dashboard **3 times consecutively** and enter security PIN **`123`** to securely access the Staff Portal.
- **Queue Management Tab:** Staff can view incoming branch orders, call/announce next numbers, mark orders as `COLLECTED`, or `CANCELLED` (with automatic stock restoration).
- **Inventory Control Tab:** Staff can toggle item availability ("For Sale") and adjust stock quantities with audit reasons (*Restocked, Sold, Spoiled, Correction*).

---

## Tech Stack

- **Language**: Kotlin (Coroutines, Flow)
- **UI Framework**: Android XML (View System), ConstraintLayout, Material Design 3
- **Mapping**: OSMDroid (OpenStreetMap)
- **Network**: Kotlin Coroutines + `HttpURLConnection` & `org.json` (Open Food Facts API)
- **Minimum SDK**: 26 (Android 8.0)
- **Target SDK**: 37 (Android 15+)

---

## Getting Started & Setup Steps

### Prerequisites
- Android Studio (Ladybug or newer)
- Android SDK 37
- Java 11 or higher

### Installation & Running
1. Clone or download the repository.
2. Open the project folder in Android Studio.
3. Allow Gradle to sync dependencies.
4. Run the app on an Android Emulator or physical device (API 26+).

---

## API Keys & Configuration

QueueEats is designed to work **out of the box with zero paid API keys**:
- **Open Food Facts API:** 100% free and open-source global food product database. Requires no API key or developer registration.
- **OpenStreetMap / OSMDroid:** Free open-source mapping tiles requiring no Google Maps API keys or billing accounts.
- **Backend / Authentication:** Operates locally via thread-safe in-memory `CanteenRepository` and `UserRepository` for instant testing and demonstration.

---

## Developer

**Kenth Daryl Velasco**  
*Lead Android Developer*

---
*Developed as part of the QueueEats Project.*
