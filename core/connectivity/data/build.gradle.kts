plugins {
    alias(libs.plugins.signaltrackermanager.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.cadrikmdev.core.connectivity.domain"
}

dependencies {

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.play.services.wearable)
    implementation(libs.bundles.koin)
    implementation(libs.timber)

    implementation(projects.core.domain)
    implementation(projects.core.connectivity.domain)
    implementation(project(":manager:domain"))
}