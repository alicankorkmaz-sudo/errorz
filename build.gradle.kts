// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

val releaseVersion = providers.gradleProperty("version").orNull
    ?: System.getenv("RELEASE_VERSION")
    ?: System.getenv("GITHUB_REF_NAME")
    ?: System.getenv("GITHUB_REF")?.substringAfterLast("/")
    ?: "0.0.0-SNAPSHOT"

allprojects {
    version = releaseVersion.removePrefix("v")
}
