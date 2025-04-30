import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("de.eldoria.plugin-yml.paper") version "0.7.1"
}

val configlibVersion: String by project
val acp2Version: String by project
val redissonVersion: String by project
val hikariVersion: String by project
val sqliteVersion: String by project
val mysqlVersion: String by project
val mariadbVersion: String by project
val mongoVersion: String by project
val influxdbVersion: String by project
val minimessageVersion: String by project
val mmPlatformVersion: String by project

repositories {
    maven(url = "https://jitpack.io")
    maven(url = "https://repo.codemc.io/repository/maven-snapshots/")
    maven(url = "https://repo.clojars.org/")
    maven(url = "https://repo.william278.net/snapshots")
    maven(url = "https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven(url = "https://repo.triumphteam.dev/snapshots/")
}

dependencies {
    implementation(project(":api"))
    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")

    annotationProcessor("info.preva1l.trashcan:paper:1.0.1")
    implementation("info.preva1l.trashcan:paper:1.0.1")

    compileOnly("net.kyori:adventure-text-serializer-gson:$minimessageVersion")

    compileOnly("me.clip:placeholderapi:2.11.6") // Placeholder support

    // Database
    library("com.zaxxer:HikariCP:$hikariVersion")
    library("org.xerial:sqlite-jdbc:$sqliteVersion")
    library("com.mysql:mysql-connector-j:$mysqlVersion")
    library("org.mariadb.jdbc:mariadb-java-client:$mariadbVersion")
    library("org.mongodb:mongodb-driver-sync:$mongoVersion")

    library("net.wesjd:anvilgui:1.10.4-SNAPSHOT") // Text Input

    compileOnly("com.github.MilkBowl:VaultAPI:1.7") // Economy Hook

    // Extra Hooks
    library("com.influxdb:influxdb-client-java:$influxdbVersion") // InfluxDB logging
}

tasks.withType<ShadowJar> {
    relocate("com.github.puregero.multilib", "info.preva1l.fadlc.libs.multilib")
    relocate("info.preva1l.hooker", "info.preva1l.fadlc.hooks.lib")
    relocate("info.preva1l.trashcan", "info.preva1l.fadlc.trashcan")

    manifest {
        attributes["paperweight-mappings-namespace"] = "spigot"
    }
}

paper {
    name = "Fadlc"
    version = rootProject.version.toString()
    description = "Fadlc (Finally a Decent Land Claim) is the fast, modern and advanced land claiming plugin that you have been looking for!"
    website = "https://docs.preva1l.info/"
    author = "Preva1l"
    main = rootProject.group.toString() + ".Fadlc"
    loader = "info.preva1l.fadlc.FadlcLibraryloader"
    generateLibrariesJson = true
    foliaSupported = true
    apiVersion = "1.19"

    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD

    serverDependencies {
        listOf(
            "PlaceholderAPI",
            "LuckPerms",
            "RedisEconomy",
            "CoinsEngine",
            "Vault",
            "PlayerPoints",
        ).forEach {
            register(it) {
                load = PaperPluginDescription.RelativeLoadOrder.AFTER
                required = false
            }
        }
    }

    permissions {
        register("fadlc.claim") {
            default = BukkitPluginDescription.Permission.Default.TRUE
        }

        register("fadlc.profiles") {
            default = BukkitPluginDescription.Permission.Default.TRUE
        }

        register("fadlc.settings") {
            default = BukkitPluginDescription.Permission.Default.TRUE
        }

        register("fadlc.particle.default") {
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
    }
}