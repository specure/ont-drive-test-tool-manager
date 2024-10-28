plugins {
    alias(libs.plugins.signaltrackermanager.android.library)
}

android {
    namespace = "com.specure.core.data"
}

dependencies {

    implementation(libs.timber)
    implementation(libs.bundles.koin)

    implementation(projects.core.domain)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}