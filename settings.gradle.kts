rootProject.name = "EnderDragonTweaks"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { // Paper
            url = uri("https://papermc.io/repo/repository/maven-public/")
        }
        maven { // Configurate
            url = uri("https://repo.spongepowered.org/maven")
        }
        maven { // run paper plugin
            url = uri("https://repo.jpenilla.xyz/snapshots/")
        }
    }
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}
