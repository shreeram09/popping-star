import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

// Versioning Logic
val versionPropsFile = file("version.properties")
val versionProps = Properties().apply {
    if (versionPropsFile.exists()) {
        load(versionPropsFile.inputStream())
    }
}

val vMajor = versionProps.getProperty("VERSION_MAJOR", "1").toInt()
val vMinor = versionProps.getProperty("VERSION_MINOR", "0").toInt()
val vPatch = versionProps.getProperty("VERSION_PATCH", "2").toInt()
val vBuild = versionProps.getProperty("VERSION_BUILD", "4").toInt()

// CI/Automation Tags
val appName = "PoppingStar"
val ciBuildNumber = System.getenv("GITHUB_RUN_NUMBER") ?: "local"
val releaseType = System.getenv("RELEASE_TYPE") ?: "alpha" // alpha, beta, prod

android {
    namespace = "com.shreeram.balloonpop"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.shreeram.balloonpop"
        minSdk = 26
        targetSdk = 36
        versionCode = vBuild
        versionName = when (releaseType) {
            "prod" -> "$vMajor.$vMinor.$vPatch"
            "beta" -> "$appName-$vMajor.$vMinor.$vPatch-beta+$ciBuildNumber"
            else -> "$appName-$vMajor.$vMinor.$vPatch-alpha+$ciBuildNumber"
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            
            ndk {
                debugSymbolLevel = "FULL"
            }
            
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coil.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
