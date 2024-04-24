plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.art_guide"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.art_guide"
        minSdk = 24
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        mlModelBinding = true
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.5.0")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")
    implementation("androidx.navigation:navigation-fragment:2.3.5")
    implementation("com.google.mlkit:image-labeling-common:18.1.0")
    testImplementation("junit:junit:4.13.2")
    implementation("com.gorisse.thomas.sceneform:sceneform:1.19.3")
    implementation ("com.google.ar:core:1.25.0")
    implementation("com.google.mlkit:object-detection-custom:17.0.1")
    implementation("com.google.mlkit:object-detection-custom:17.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.flaviofaria:kenburnsview:1.0.7")
    implementation("com.google.android.material:material:1.0.0")
    implementation("androidx.palette:palette:1.0.0")
    }