plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.turtlepaw.wifihelper"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.turtlepaw.wifihelper"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.play.services.wearable)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.compose.material)
    implementation(libs.compose.foundation)
    implementation(libs.wear.tooling.preview)
    implementation(libs.activity.compose)
    implementation(libs.core.splashscreen)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Horologist
    implementation(libs.horologist.health.service)
    implementation(libs.horologist.health.composables)
    implementation(libs.horologist.compose.material)
    implementation(libs.horologist.compose.layout)
    implementation(libs.horologist.composables)
    implementation(libs.horologist.compose.tools)
    implementation(libs.horologist.tiles)

    // Icons
    implementation(libs.material.icons.extended)
    debugImplementation(libs.androidx.tiles.tooling)

    // Permissions
    implementation(libs.accompanist.permissions)
}