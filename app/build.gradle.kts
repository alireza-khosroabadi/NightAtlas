plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose)
    alias(libs.plugins.serialization)
}

android {
    namespace = "dev.nightatlas.app"
    compileSdk = 37
    defaultConfig {
        minSdk = 24
        applicationId = "dev.nightatlas"
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures { compose = true }
}

dependencies {
    implementation(libs.hilt.android)
    implementation(libs.hilt.viewmodel.compose)
    ksp(libs.hilt.compiler)
    implementation(platform(libs.compose.bom))
    implementation(libs.material3)
    implementation(libs.sheets)
    implementation(libs.navigation)
    implementation(libs.serialization)
    implementation(libs.activity)
    implementation(libs.lifecycle.compose)
    implementation(project(":core:navigation"))
    implementation(project(":core:ui"))
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":feature:atlas"))
    implementation(project(":feature:journal"))
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test)
    androidTestImplementation(libs.android.test.ext)
    androidTestImplementation(libs.android.test.runner)
    testImplementation(libs.junit)
}
