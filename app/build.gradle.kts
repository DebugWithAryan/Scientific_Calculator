plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.collegegraduate.scientificcalculator"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.collegegraduate.scientificcalculator"
        minSdk = 24
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    implementation("net.objecthunter:exp4j:0.4.8")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}