package com.specure.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.DynamicFeatureExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureBuildTypes(
    commonExtension: CommonExtension<*,*,*,*,*,*>,
    extensionType: ExtensionType
) {
    commonExtension.run {

        buildFeatures {
            buildConfig = true
        }

        val githubTrackerRepoApiUrl =
            gradleLocalProperties(rootDir, providers).getProperty("GITHUB_API_TRACKER_REPO_URL")
        val githubManagerRepoApiUrl =
            gradleLocalProperties(rootDir, providers).getProperty("GITHUB_API_MANAGER_REPO_URL")
        val githubAccessToken =
            gradleLocalProperties(rootDir, providers).getProperty("GITHUB_API_TOKEN")

        when(extensionType) {
            ExtensionType.APPLICATION -> {
                extensions.configure<ApplicationExtension> {
                    buildTypes {
                        debug {
                            configureDebugBuildType(
                                githubTrackerRepoApiUrl,
                                githubManagerRepoApiUrl,
                                githubAccessToken
                            )
                        }
                        release {
                            configureReleaseBuildType(
                                commonExtension,
                                githubTrackerRepoApiUrl,
                                githubManagerRepoApiUrl,
                                githubAccessToken
                            )
                        }
                    }
                }
            }
            ExtensionType.LIBRARY -> {
                extensions.configure<LibraryExtension> {
                    buildTypes {
                        debug {
                            configureDebugBuildType(
                                githubTrackerRepoApiUrl,
                                githubManagerRepoApiUrl,
                                githubAccessToken
                            )
                        }
                        release {
                            configureReleaseBuildType(
                                commonExtension,
                                githubTrackerRepoApiUrl,
                                githubManagerRepoApiUrl,
                                githubAccessToken
                            )
                        }
                    }
                }
            }

            ExtensionType.DYNAMIC_FEATURE -> {
                extensions.configure<DynamicFeatureExtension> {
                    buildTypes {
                        debug {
                            configureDebugBuildType(
                                githubTrackerRepoApiUrl,
                                githubManagerRepoApiUrl,
                                githubAccessToken
                            )
                        }
                        release {
                            configureReleaseBuildType(
                                commonExtension,
                                githubTrackerRepoApiUrl,
                                githubManagerRepoApiUrl,
                                githubAccessToken
                            )
                            isMinifyEnabled = false
                        }
                    }
                }
            }
        }
    }
}

private fun BuildType.configureDebugBuildType(
    githubTrackerRepoApiUrl: String,
    githubManagerRepoApiUrl: String,
    githubAccessToken: String
) {
    buildConfigField("String", "GITHUB_API_TRACKER_REPO_URL", "\"$githubTrackerRepoApiUrl\"")
    buildConfigField("String", "GITHUB_API_MANAGER_REPO_URL", "\"$githubManagerRepoApiUrl\"")
    buildConfigField("String", "GITHUB_API_TOKEN", "\"$githubAccessToken\"")
}

private fun BuildType.configureReleaseBuildType(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    githubTrackerRepoApiUrl: String,
    githubManagerRepoApiUrl: String,
    githubAccessToken: String
) {
    buildConfigField("String", "GITHUB_API_TRACKER_REPO_URL", "\"$githubTrackerRepoApiUrl\"")
    buildConfigField("String", "GITHUB_API_MANAGER_REPO_URL", "\"$githubManagerRepoApiUrl\"")
    buildConfigField("String", "GITHUB_API_TOKEN", "\"$githubAccessToken\"")
    isMinifyEnabled = false
    proguardFiles(
        commonExtension.getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
    )
}