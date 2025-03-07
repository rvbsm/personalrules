plugins {
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.publish)
}

private val gitVersion: String by gradle.extra
private val minecraftProjectVersion: String = "1.21.4"
private val minecraftTargetVersion: String = "1.21.4"

private val javaVersion = property("java.version").toString().toInt(10)
private val modrinthId = property("mod.modrinth_id").toString()

version = "$gitVersion+mc$minecraftProjectVersion"
group = "dev.rvbsm"
base.archivesName = rootProject.name

loom {
    splitEnvironmentSourceSets()
    mods.register(name) {
        sourceSet("main")
    }

    runConfigs.all {
        ideConfigGenerated(true)
        runDir = "run"
    }
}

repositories {
    mavenCentral()
}

dependencies {
    minecraft("com.minecraft:minecraft:$minecraftTargetVersion")
    mappings("net.fabricmc:yarn:$minecraftTargetVersion+build.${property("fabric.yarn_build")}:v2")

    modImplementation(libs.fabric.loader)
}

tasks {
    processResources {
        val properties = mapOf(
            "version" to "$version",
            "javaVersion" to javaVersion,
            "minecraftVersion" to minecraftProjectVersion,
        )

        inputs.properties(properties)
        filesMatching("fabric.mod.json") {
            expand(properties)
        }
    }

    jar {
        from(rootProject.file("LICENSE")) {
            rename { "${it}_${rootProject.name}" }
        }
    }
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.toVersion(javaVersion)
    targetCompatibility = JavaVersion.toVersion(javaVersion)
}

publishMods {
    dryRun = !providers.environmentVariable("MODRINTH_TOKEN").isPresent

    file = tasks.remapJar.flatMap { it.archiveFile }
    additionalFiles.from(tasks.remapSourcesJar.map { it.archiveFile })
    changelog = providers.environmentVariable("CHANGELOG")
    type = when {
        "alpha" in gitVersion -> ALPHA
        "beta" in gitVersion -> BETA
        else -> STABLE
    }
    displayName = "[$minecraftProjectVersion] v$gitVersion"
    modLoaders.addAll("fabric", "quilt")

    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        projectId = modrinthId
        featured = true

        minecraftVersionRange {
            start = minecraftProjectVersion
            end = minecraftTargetVersion
        }
    }
}
