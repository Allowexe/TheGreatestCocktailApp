# The Greatest CocktailApp

A modern Android application for exploring, searching, and managing cocktail recipes. This project utilizes **TheCocktailDB API** to provide real-time drink data and instructions.

## Core Functionality

* **Randomized Discovery**: Generates a random cocktail on the landing screen.
* **Categorized Exploration**: Browse full lists of drinks based on official categories.
* **Dual Search Engine**: 
    * Search by drink name for specific recipes.
    * Search by ingredient to find cocktails based on available stock.
* **Local Persistence**: A favorites system powered by `SharedPreferences` to save specific cocktails.
* **System Integration**: Native Android Share Intent to export recipes as plain text.

---

## Technical Stack

| Component | Library / Framework |
| :--- | :--- |
| **Language** | Kotlin 2.1+ |
| **UI Framework** | Jetpack Compose (Material 3) |
| **Networking** | Retrofit 2 / OkHttp |
| **JSON Parsing** | GSON |
| **Image Loading** | Coil 3 |
| **Navigation** | Jetpack Navigation Compose |

---

## UI/UX Design

The application follows a custom **Cyber-Purple** design language:
* **Theme**: Deep dark backgrounds (`#0F0F0F`) with high-contrast purple accents (`#9D4EDD`).
* **Glassmorphism**: Custom `Card` components with 5% white opacity and subtle borders for a layered frosted-glass effect.
* **Motion**: 
    * `AnimatedContent` for smooth transitions between loading states and data.
    * Staggered list entry animations using `AnimatedVisibility` and vertical slides.
    * `animateContentSize` for expandable UI elements.

---

## Development Setup

### Prerequisites
* Android Studio Ladybug or later.
* JDK 17+.
* Active Internet connection for API calls.

### Build Instructions
1. Clone the repository.
2. Open the project in Android Studio.
3. Sync Gradle files.
4. Run the `app` configuration on an emulator or physical device.

---

## API Reference

This application consumes the public [TheCocktailDB API](https://www.thecocktaildb.com/api.php). 
* **Authentication**: None (Developer Test Key "1").
* **Endpoints**: `random.php`, `list.php`, `search.php`, `filter.php`, `lookup.php`.

---

## License
Distributed under the MIT License. See `LICENSE` for more information.
