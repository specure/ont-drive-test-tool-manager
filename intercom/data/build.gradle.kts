plugins {
    alias(libs.plugins.signaltrackermanager.android.library)
    alias(libs.plugins.signaltrackermanager.jvm.ktor)
}

android {
    namespace = "com.cadrikmdev.intercom.data"
}

dependencies {
    implementation(libs.timber)
    implementation(libs.bundles.koin)

    implementation(projects.core.domain)
    implementation(projects.intercom.domain)
}