pluginManagement {
    repositories {
        providers.gradleProperty("googleMavenMirror").orNull?.let { maven(it) }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        providers.gradleProperty("googleMavenMirror").orNull?.let { maven(it) }
        google()
        mavenCentral()
    }
}

rootProject.name = "NightAtlas"

include(
    ":app",
    ":core:navigation",
    ":core:ui",
    ":domain",
    ":data",
    ":feature:atlas",
    ":feature:journal",
)
