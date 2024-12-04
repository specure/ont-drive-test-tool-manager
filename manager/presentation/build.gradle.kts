plugins {
    alias(libs.plugins.signaltrackermanager.android.feature.ui)
}

android {
    namespace = "com.specure.manager.presentation"
}

dependencies {

    implementation(libs.coil.compose)
    implementation(libs.google.maps.android.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.timber)
    implementation(libs.preference.library)

    implementation(projects.intercom.domain)

    implementation(projects.core.domain)
    implementation(projects.core.presentation.ui)

    implementation(projects.manager.domain)
    implementation(projects.permissions.domain)
    implementation(projects.permissions.presentation)
    implementation(projects.updater.domain)
    implementation(projects.intercom.presentation)
    implementation(projects.intercom.data)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}