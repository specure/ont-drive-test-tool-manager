plugins {
    alias(libs.plugins.signaltrackermanager.jvm.library)
    alias(libs.plugins.signaltrackermanager.jvm.junit5)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)

    implementation(projects.core.domain)
    implementation(projects.intercom.domain)
}