plugins {
    `kotlin-dsl`
}

group = "com.cadrikmdev.signaltrackermanager.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "signaltrackermanager.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "signaltrackermanager.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidApplicationWearCompose") {
            id = "signaltrackermanager.android.application.wear.compose"
            implementationClass = "AndroidApplicationWearComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "signaltrackermanager.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "signaltrackermanager.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidFeatureUi") {
            id = "signaltrackermanager.android.feature.ui"
            implementationClass = "AndroidFeatureUiConventionPlugin"
        }
        register("androidRoom") {
            id = "signaltrackermanager.android.room"
            implementationClass = "AndroidRoomConventionPlugin"
        }
        register("androidDynamicFeature") {
            id = "signaltrackermanager.android.dynamic.feature"
            implementationClass = "AndroidDynamicFeatureConventionPlugin"
        }
        register("jvmLibrary") {
            id = "signaltrackermanager.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("jvmKtor") {
            id = "signaltrackermanager.jvm.ktor"
            implementationClass = "JvmKtorConventionPlugin"
        }
        register("jvmJunit5") {
            id = "signaltrackermanager.jvm.junit5"
            implementationClass = "JvmJUnit5ConventionPlugin"
        }
        register("androidJunit5") {
            id = "signaltrackermanager.android.junit5"
            implementationClass = "androidJUnit5ConventionPlugin"
        }
    }
}