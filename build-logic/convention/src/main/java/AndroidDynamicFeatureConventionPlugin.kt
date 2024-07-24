import com.android.build.api.dsl.DynamicFeatureExtension
import com.cadrikmdev.convention.ExtensionType
import com.cadrikmdev.convention.addUiLayerDependencies
import com.cadrikmdev.convention.configureAndroidCompose
import com.cadrikmdev.convention.configureBuildTypes
import com.cadrikmdev.convention.configureKotlinAndroid
import com.cadrikmdev.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidDynamicFeatureConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("com.android.dynamic-feature")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            extensions.configure<DynamicFeatureExtension> {
                configureKotlinAndroid(this)

                configureAndroidCompose(this)

                configureBuildTypes(
                    commonExtension = this,
                    extensionType = ExtensionType.DYNAMIC_FEATURE
                )
            }

            dependencies {
                addUiLayerDependencies(target)
                "testImplementation"(kotlin("test"))
                "implementation"(libs.findLibrary("core.feature.delivery").get())
            }
        }
    }
}