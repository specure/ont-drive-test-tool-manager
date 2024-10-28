plugins {
    alias(libs.plugins.signaltrackermanager.android.feature.ui)
    alias(libs.plugins.mapsplatform.secrets.plugin)
}

android {
    namespace = "com.specure.intercom.presentation"
}

dependencies {

    implementation(libs.coil.compose)
    implementation(libs.google.maps.android.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.timber)

    implementation(projects.core.domain)
    implementation(projects.intercom.domain)
    implementation(projects.permissions.domain)
    implementation(projects.permissions.presentation)
}