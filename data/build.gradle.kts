plugins { alias(libs.plugins.android.library) }

android {
    namespace = "dev.nightatlas.data"
    compileSdk = 37
    defaultConfig { minSdk = 24 }
}

dependencies {
    implementation(libs.coroutines)
    implementation(project(":domain"))
    testImplementation(libs.junit)
}
