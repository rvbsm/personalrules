rootProject.name = "personalrules"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net")
    }
}

val gitVersion: String by gradle.extra {
    providers.exec {
        executable = "git"
        args = listOf("describe", "--tags", "--dirty", "--always")
    }.standardOutput.asText.map { it.trim().drop(1) }.get()
}
