package com.shreeram.balloonpop.settings

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

enum class BackgroundMode {
    DEFAULT, RANDOM_SOLID, CUSTOM_SOLID, IMAGE
}

enum class OrientationMode {
    SYSTEM, PORTRAIT, LANDSCAPE
}

data class AppSettings(
    val soundEnabled: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val orientationMode: OrientationMode = OrientationMode.SYSTEM,
    val backgroundMode: BackgroundMode = BackgroundMode.DEFAULT,
    val backgroundColor: Int = 0xFFFFFFFF.toInt(),
    val backgroundImageUri: String? = null
)
