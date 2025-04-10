pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

gradle.startParameter.excludedTaskNames.addAll(listOf(":build-logic:convention:testClasses"))

rootProject.name = "SignalTrackerManager"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":app")
include(":core:data")
include(":core:domain")
include(":core:presentation:ui")
include(":core:presentation:designsystem")
include(":manager:data")
include(":manager:presentation")
include(":manager:domain")
include(":permissions:data")
include(":permissions:domain")
include(":permissions:presentation")
include(":intercom:data")
include(":intercom:domain")
include(":updater:data")
include(":updater:domain")

project(":intercom:data").projectDir = file("bluetoothIntercom/intercom/data")
project(":intercom:domain").projectDir = file("bluetoothIntercom/intercom/domain")
