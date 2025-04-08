rootProject.name = "personalrules"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net")
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.kikugie.dev/snapshots")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.6-beta.+"
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    shared {
        vcsVersion = "1.21.5"
        versions("1.21.4", "1.21.5")
    }
    create(rootProject)
}

