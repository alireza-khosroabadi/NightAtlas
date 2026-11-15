plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.library)
    alias(libs.plugins.serialization)
}

android {
    namespace = "dev.nightatlas.core.navigation"
    compileSdk = 37
    defaultConfig { minSdk = 24 }
}

dependencies {
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.navigation)
    implementation(libs.serialization)
    testImplementation(libs.junit)
}
