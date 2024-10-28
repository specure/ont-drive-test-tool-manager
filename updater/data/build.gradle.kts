plugins {
    alias(libs.plugins.signaltrackermanager.android.library)
    alias(libs.plugins.signaltrackermanager.jvm.ktor)
}

android {
    namespace = "com.specure.updater.data"
}

dependencies {

    implementation(projects.updater.domain)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.timber)
    implementation(libs.bundles.koin)
    implementation(libs.process.phoenix)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}