import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
import java.io.ByteArrayOutputStream

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
//    shadow("cat.inspiracio:rhino-js-engine:1.7.10") // Rhino
    compileOnly("com.semivanilla.lootitems:LootItems:1.0-SNAPSHOT")
}

repositories {
    mavenCentral()
    maven { // Semi Vanilla
        url = uri("https://sv.destro.xyz/snapshots/")
    }
    maven { // Paper
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
//    maven { // Configurate
//        url = uri("https://repo.spongepowered.org/maven")
//    }
    maven { // run paper plugin
        url = uri("https://repo.jpenilla.xyz/snapshots/")
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
        minimize {
            exclude(dependency("org.mozilla.javascript:.*:.*"))
        }
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
    version = "${rootProject.version}-${gitCommit()}"
    apiVersion = "1.18"
    website = "https://github.com/SemiVanilla-MC/${rootProject.name}"
    authors = listOf("destro174")
    commands {
        create("enderdragontweaks") {
            description = "Base command for the enderdragontweaks plugin."
            usage = "/enderdragontweaks"
            permission = "enderdragontweaks.command"
        }
    }
}

fun gitCommit(): String {
    val os = ByteArrayOutputStream()
    project.exec {
        commandLine = "git rev-parse --short HEAD".split(" ")
        standardOutput = os
    }
    return String(os.toByteArray()).trim()
}