import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("io.papermc.paperweight.userdev") version "1.3.3"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
    id("xyz.jpenilla.run-paper") version "1.0.6"
}

group = "com.semivanilla.enderdragontweaks"
version = "1.0-SNAPSHOT"
description = ""

dependencies {
    paperDevBundle("1.18.1-R0.1-SNAPSHOT") // Paper
//    compileOnly("io.papermc.paper:paper-api:1.18.1-R0.1-SNAPSHOT") // Paper
    shadow("net.kyori:adventure-text-minimessage:4.2.0-SNAPSHOT") { // Minimessage
        exclude("net.kyori", "adventure-api")
    }
}

tasks {

    assemble {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }

    runServer {
        minecraftVersion("1.18.1")
    }

    shadowJar {
        dependsOn(getByName("relocateJars") as ConfigureShadowRelocation)
        archiveFileName.set("${project.name}-${project.version}.jar")
        minimize()
        configurations = listOf(project.configurations.shadow.get())
    }

    build {
        dependsOn(shadowJar)
    }

    create<ConfigureShadowRelocation>("relocateJars") {
        target = shadowJar.get()
        prefix = "${project.name}.lib"
    }
}

bukkit {
    name = rootProject.name
    main = "$group.${rootProject.name}"
    apiVersion = "1.18"
    website = "https://github.com/SemiVanilla-MC/${rootProject.name}"
    authors = listOf("destro174")
}