plugins {
    alias(libs.plugins.signaltrackermanager.android.application.compose)
}

android {
    namespace = "com.specure.signaltrackermanager"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true // Disable R8 for release builds
            isShrinkResources = true
        }
    }
}

dependencies {


    // Coil
    implementation(libs.coil.compose)

    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)

    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Crypto
    implementation(libs.androidx.security.crypto.ktx)

    // Koin
    implementation(libs.bundles.koin)

    api(libs.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.feature.delivery.ktx)
    implementation(projects.permissions.presentation)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Location
    implementation(libs.google.android.gms.play.services.location)

    // Splash screen
    implementation(libs.androidx.core.splashscreen)

    // Timber
    implementation(libs.timber)

    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(projects.core.presentation.designsystem)
    implementation(projects.core.presentation.ui)

    implementation(projects.manager.presentation)
    implementation(projects.manager.data)
    implementation(projects.manager.domain)

    implementation(projects.intercom.domain)
    implementation(projects.intercom.data)

    implementation(projects.updater.data)
    implementation(projects.updater.domain)

    implementation(platform(libs.firebase))
}