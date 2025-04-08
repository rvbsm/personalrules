plugins {
    id("dev.kikugie.stonecutter")
    alias(libs.plugins.fabric.loom) apply false
    alias(libs.plugins.j52j) apply false
    alias(libs.plugins.publish) apply false
}
stonecutter active "1.21.5" /* [SC] DO NOT EDIT */

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) { 
    group = "project"
    ofTask("build")
}

val gitVersion: String by extra {
    providers.exec {
        executable = "git"
        args = listOf("describe", "--tags", "--dirty", "--always")
    }.standardOutput.asText.map { it.trim().drop(1) }.get()
}
