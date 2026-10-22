plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.aula.playlist"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.aula.playlist"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

// Plugin do Firebase: só é aplicado quando o google-services.json estiver em app/.
// Sem o arquivo o projeto continua compilando e o app usa o repositório Mock (em memória).
if (file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Firebase (versões controladas pela BoM)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}