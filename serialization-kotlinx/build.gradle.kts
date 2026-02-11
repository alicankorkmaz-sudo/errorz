plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    `maven-publish`
}

android {
    namespace = "com.alicankorkmaz.errorz.serialization.kotlinx"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.alicankorkmaz.errorz"
            artifactId = "serialization-kotlinx"
            version = "0.1.0"

            afterEvaluate {
                from(components["release"])
            }

            pom {
                name.set("Errorz Kotlinx Serialization")
                description.set("Kotlinx Serialization integration for Errorz — error body parsing, serialization exception mapping")
                url.set("https://github.com/alicankorkmaz-sudo/errorz")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/alicankorkmaz-sudo/errorz")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

dependencies {
    api(project(":core"))
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
}
