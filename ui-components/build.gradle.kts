import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}


kotlin {
    applyDefaultHierarchyTemplate()

    iosArm64()
    iosSimulatorArm64()
    jvm()

    android {
        namespace = "com.cashierserviceapp.ui"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.compose.runtime)
            api(libs.compose.foundation)
            api(libs.compose.components.resources)
            api(libs.compose.animation)
            api(libs.compose.material3)
            api(libs.compose.ui)
            implementation(libs.compose.uiToolingPreview)

            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.material3.adaptive)
            implementation(libs.material3.adaptive.layout)
            implementation(libs.material3.adaptive.navigation)

            implementation(libs.settings)

            // Pure-Kotlin QR encoder: `api` so :shared can reach the raw module matrix for the
            // printer path, not just the composable.
            api(libs.qrcode.kotlin)
        }
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.coil.network.okhttp)
            implementation(libs.metrox.android)
        }
    }
}

compose.resources {
    publicResClass = true
    nameOfResClass = "UiRes"
    packageOfResClass = "com.cashierserviceapp.ui.generated.resources"
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}