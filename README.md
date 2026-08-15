# PoppingStar

A polished, responsive balloon-popping game built with Kotlin, Jetpack Compose, and Canvas.

## Project Structure
- **game/**: Framework-independent gameplay logic, delta-time engine, and Canvas rendering.
- **profile/**: Local user profile management with name uniqueness and UUIDs.
- **settings/**: App-wide settings for theme, sound, and orientation.
- **ui/**: Material 3 UI screens and Jetpack Navigation.
- **storage/**: Data persistence using Jetpack DataStore.

---

## 🚀 How to Run

### 1. Run with a Virtual Device (Emulator)
1.  Open the project in **Android Studio**.
2.  Go to **Tools > Device Manager**.
3.  Click **Create device** and follow the wizard to set up an Android Virtual Device (AVD) (API 34+ recommended).
4.  Select your virtual device in the toolbar dropdown.
5.  Click the **Run** button (green play icon) or press `Shift + F10`.

### 2. Run with an Attached USB Device
1.  On your Android device, enable **Developer Options** (Settings > About Phone > Tap 'Build Number' 7 times).
2.  Enable **USB Debugging** in Developer Options.
3.  Connect your device to your computer via USB.
4.  Accept the "Allow USB debugging" prompt on your device.
5.  In Android Studio, select your physical device in the toolbar dropdown.
6.  Click the **Run** button.

### 3. Run with VS Code
1.  Set the Android SDK environment variables. On Windows PowerShell, use the Android SDK path installed by Android Studio:
    ```powershell
    $env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
    $env:ANDROID_SDK_ROOT = $env:ANDROID_HOME
    ```
    To persist them for future terminals, use:
    ```powershell
    [Environment]::SetEnvironmentVariable("ANDROID_HOME", "$env:LOCALAPPDATA\Android\Sdk", "User")
    [Environment]::SetEnvironmentVariable("ANDROID_SDK_ROOT", "$env:LOCALAPPDATA\Android\Sdk", "User")
    ```
    In Git Bash, use:
    ```bash
    export ANDROID_HOME="$LOCALAPPDATA/Android/Sdk"
    export ANDROID_SDK_ROOT="$ANDROID_HOME"
    ```
    If Android Studio uses a different SDK location, replace the path with the value shown in **Settings > Languages & Frameworks > Android SDK**.
2.  Append the Android SDK tools to `PATH`. In Windows PowerShell, for the current terminal session use:
    ```powershell
    $env:Path += ";$env:ANDROID_HOME\platform-tools;$env:ANDROID_HOME\cmdline-tools\latest\bin;$env:ANDROID_HOME\emulator"
    ```
    To persist the paths for future terminals, use:
    ```powershell
    $userPath = [Environment]::GetEnvironmentVariable("Path", "User")
    $androidPaths = "$env:ANDROID_HOME\platform-tools;$env:ANDROID_HOME\cmdline-tools\latest\bin;$env:ANDROID_HOME\emulator"
    [Environment]::SetEnvironmentVariable("Path", "$userPath;$androidPaths", "User")
    ```
    In Git Bash, use:
    ```bash
    export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/emulator:$PATH"
    ```
3.  Install the **Extension Pack for Java** and Android development extensions, and ensure the Android SDK and platform tools are installed.
4.  Open the project folder in VS Code.
5.  Start an emulator, or list and launch an installed AVD from the VS Code terminal:
    ```bash
    emulator -list-avds
    emulator -avd <avd-name>
    adb devices
    ```
6.  Build and install the debug APK:
    ```bash
    ./gradlew :app:installDebug
    ```
    On Windows PowerShell, use:
    ```powershell
    .\gradlew.bat :app:installDebug
    ```
7.  Launch the app:
    ```bash
    adb shell am start -n com.shreeram.balloonpop/.MainActivity
    ```
8.  Use **Run and Debug** for a configured Android/Java debug configuration, or run Gradle tasks directly from the integrated terminal.

---

## 📦 Distribution

### 4. Generate a Copyable Install Package (APK)
To create a file you can send to any device for installation:
1.  Go to **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
2.  Wait for the build to finish. A notification will appear in the bottom right.
3.  Click **Locate** to find the `app-debug.apk` (or `app-release-unsigned.apk`).
4.  Copy this file to any Android device and open it to install the game.
    *   *Note: You may need to enable "Install from Unknown Sources" on the device.*

### 5. Publish to Google Play Store
1.  **Generate a Signed Bundle**:
    - Go to **Build > Generate Signed Bundle / APK...**
    - Select **Android App Bundle** and click **Next**.
    - Create a new KeyStore or use an existing one.
    - Follow the prompts to sign the release build.
2.  **Google Play Console**:
    - Log in to the [Google Play Console](https://play.google.com/console).
    - Create a new App and follow the setup instructions (Store listing, Content rating, etc.).
    - Go to **Production > Releases** and upload the signed `.aab` file located in `app/release/`.
    - Complete the rollout process for review.

---

## 🛠️ Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Graphics**: Android Canvas
- **Persistence**: DataStore (Preferences & Serialization)
- **Audio**: SoundPool
- **Navigation**: Jetpack Navigation Compose
- **Architecture**: MVVM with Unidirectional Data Flow (UDF)

## License and Contributions

Copyright (c) 2026 Shreeram. This project is licensed under the GNU General
Public License v3.0. See [LICENSE](LICENSE) and [COPYRIGHT.md](COPYRIGHT.md).

Contributions are welcome under the Developer Certificate of Origin. Every
commit must include a `Signed-off-by` line. See [CONTRIBUTING.md](CONTRIBUTING.md)
for the contribution and copyright terms.
