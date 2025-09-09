# Snake – Android Game (Java)

An Android implementation of the classic **Snake** game built with **Java** and **Android Studio**.  
Features a main menu, color options, pause & game-over overlays, high score persistence, sound effects, and swipe controls.

---

## 🎮 Gameplay

- Move the snake on a grid, **eat fruit to grow** and gain points (**+10** per fruit).
- **Hit a wall → game over.**
- **Cross your own body:** the snake **bites its tail** (it shortens) and your score is **recomputed** to `length × 10` (score decreases).
- **High score** is stored persistently and shown in-game.

**Controls (touch / swipe):**
- Swipe **right / left / up / down** to change direction.
- Tap the **pause button (Ⅱ)** in the **top-right corner** to pause.

**Menus:**
- **Pause overlay:** _Resume_, _Main Menu_, _Exit_.
- **Game Over overlay:** _Restart_, _Main Menu_.

---

## 🧰 Tech stack

- **Language:** Java
- **IDE:** Android Studio (Gradle build)
- **Gradle:** with version catalog (`libs.versions.toml`)
- **compileSdk / targetSdk:** **35**
- **minSdk:** **24**
- **UI:** Views + custom `SurfaceView` (`SnakeView`)
- **Audio:** `SoundPool` (fruit bite sound)
- **Persistence:** `SharedPreferences` (high score, color options)
- **Dependencies:** `androidx.appcompat`, `material`, `activity`, `constraintlayout`, JUnit & Espresso for tests


---

## ⚙️ Settings (Options)

From **Options**:
- **Snake color:** `green` (default), `red`, `blue`, `white`
- **Fruit color:** `red` (default), `blue`, `white`

Settings are saved in `SharedPreferences` (`SnakePrefs`), keys: `snake_color`, `fruit_color`.  
**High score** is kept under key `highScore`.

---

## 🧠 How it works (internals)

- **Rendering:** custom `SurfaceView` (`SnakeView`) with a 2D grid (default ~20×28 cells).
- **Game loop:** `run()` updates at a fixed step (`stepTime ≈ 140 ms`) and draws at ~60 FPS.
- **Input:** swipe detection in `onTouchEvent` to change direction; taps for pause/menu buttons.
- **Collisions:** walls end the game; self-collision trims the snake to the bite index and recalculates score.
- **Fruit spawn:** random cell that is **not** on the snake (`generateFruit()` ensures no overlap).
- **Audio:** `SoundPool` plays **bite** sound when fruit is eaten.

---

## 🚀 Run the app

1. **Open in Android Studio**  
   - `File → Open…` → choose the `Snake/` project root.  
   - Let Gradle sync; ensure an SDK with **API 35** is installed (compile/target).  
2. **Run on device or emulator**  
   - `Run ▶` Main app module.  
   - Alternatively via CLI: `./gradlew assembleDebug` and install the generated APK.


## 🧑‍💻 Author

Created by **Adam Kuchár**  
