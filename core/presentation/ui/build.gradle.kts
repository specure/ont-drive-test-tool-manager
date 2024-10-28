plugins {
    alias(libs.plugins.signaltrackermanager.android.library.compose)
}

android {
    namespace = "com.specure.core.presentation.ui"
}

dependencies {

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    implementation(projects.core.domain)
    implementation(projects.core.presentation.designsystem)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}