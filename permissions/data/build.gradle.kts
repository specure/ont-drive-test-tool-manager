plugins {
    alias(libs.plugins.signaltrackermanager.android.library)
}

android {
    namespace = "com.cadrikmdev.signaltrackermanager.permissions.data"
}

dependencies {

    implementation(projects.core.domain)
    implementation(projects.permissions.domain)
}