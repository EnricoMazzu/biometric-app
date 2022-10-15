plugins {
    id(PluginsDef.androidApplication)
    id(PluginsDef.kotlinAndroid)
    kotlin(PluginsDef.kapt)
    id(PluginsDef.daggerHilt)
}

android {
    namespace = "com.mzzlab.sample.biometric"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.mzzlab.sample.biometric"
        minSdk = 24
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.1"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    //Timber logger
    implementation(Dependencies.timber)
    // core ktx
    implementation(Dependencies.androidxCoreKtx)
    //lifecycle
    implementation(Dependencies.androidxLifecycleRuntimeKtx)
    implementation(Dependencies.androidxLifecycleRuntimeCompose)
    //compose
    implementation(Dependencies.androidxActivityCompose)
    implementation(Dependencies.androidxComposeUi)
    implementation(Dependencies.androidxComposeUiToolingPreview)
    implementation(Dependencies.androidxComposeMaterial)
    // compose + viewmodel & lifecycle
    implementation(Dependencies.androidxLifecycleViewmodelCompose)
    // Navigation compose
    implementation(Dependencies.androidxNavigationCompose)
    // Hilt & navigation compose
    implementation(Dependencies.hiltAndroid)
    kapt(Dependencies.hiltCompiler)
    implementation(Dependencies.androidxHiltNavCompose)
    // Biometric
    implementation(Dependencies.androidxBiometric)
    //Test & dev support
    testImplementation(Dependencies.junit)
    androidTestImplementation(Dependencies.junitExt)
    androidTestImplementation(Dependencies.espressoCore)
    androidTestImplementation(Dependencies.composeUiTestJunit)
    debugImplementation(Dependencies.androidxComposeUiTooling)
    debugImplementation(Dependencies.androidxComposeUiTestManifest)
}