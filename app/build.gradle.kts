import java.util.Properties
import java.io.FileInputStream



plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.example.notez"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.notez"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val properties=Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        buildConfigField ("String","API_KEY","\"${properties.getProperty("apiKey")}\"")

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    // Enable BuildConfig generation
    buildFeatures {
        compose = true
        buildConfig = true // Enable BuildConfig generation
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// Utility function to load the API key from local.properties



// Utility function to load API key from local.properties


dependencies {
    // Core and Compose dependencies

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.8.3")
    implementation("androidx.compose.material:material-icons-core:1.7.4")
    implementation("androidx.compose.material:material-icons-extended:1.7.4")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.31.0-alpha")
    implementation("androidx.compose.material:material:1.7.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")
    implementation ("androidx.compose.runtime:runtime-livedata:1.7.4")

    // Room dependencies
    implementation("androidx.room:room-runtime:2.5.0")
    implementation("androidx.room:room-ktx:2.5.0")
    ksp("androidx.room:room-compiler:2.5.0")

    // Firebase dependencies using BOM
    implementation(platform("com.google.firebase:firebase-bom:32.0.0")) // Update if newer version available
    implementation("com.google.firebase:firebase-auth-ktx") // KTX variant
    implementation("com.google.firebase:firebase-firestore-ktx") // KTX variant
    implementation("com.google.firebase:firebase-analytics-ktx") // KTX variant
    implementation("com.google.firebase:firebase-storage-ktx") // KTX variant
    implementation("com.google.firebase:firebase-crashlytics-ktx") // KTX variant

    // Koin dependencies
    implementation("io.insert-koin:koin-core:3.5.0")
    implementation("io.insert-koin:koin-android:3.5.0")
    implementation("io.insert-koin:koin-androidx-compose:3.5.0")

    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
}








