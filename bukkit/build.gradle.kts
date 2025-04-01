import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val minecraftVersion: String by project
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
    compileOnly("io.papermc.paper:paper-api:$minecraftVersion")

    implementation("com.github.puregero:multilib:1.2.4") // Folia & Shreddedpaper support
    implementation("dev.triumphteam:triumph-cmd-bukkit:2.0.0-SNAPSHOT") // Command lib
    implementation("net.william278:desertwell:2.0.4") // Update Checker & About Page

    // Cross Server Support
    compileOnly("org.redisson:redisson:$redissonVersion")
    compileOnly("org.apache.commons:commons-pool2:$acp2Version")
    compileOnly("net.kyori:adventure-text-serializer-gson:$minimessageVersion")

    compileOnly("me.clip:placeholderapi:2.11.6") // Placeholder support

    // Database
    compileOnly("com.zaxxer:HikariCP:$hikariVersion")
    compileOnly("org.xerial:sqlite-jdbc:$sqliteVersion")
    compileOnly("com.mysql:mysql-connector-j:$mysqlVersion")
    compileOnly("org.mariadb.jdbc:mariadb-java-client:$mariadbVersion")
    compileOnly("org.mongodb:mongodb-driver-sync:$mongoVersion")

    implementation("net.wesjd:anvilgui:1.10.4-SNAPSHOT") // Text Input

    compileOnly("de.exlll:configlib-yaml:$configlibVersion") // config

    compileOnly("com.github.MilkBowl:VaultAPI:1.7") // Economy Hook

    // Extra Hooks
    annotationProcessor("info.preva1l.hooker:Hooker:1.0.2")
    implementation("info.preva1l.hooker:Hooker:1.0.2")
    compileOnly("com.influxdb:influxdb-client-java:$influxdbVersion") // InfluxDB logging
}

tasks.withType<ShadowJar> {
    relocate("net.wesjd", "info.preva1l.fadlc.menus.lib.anvilgui")
    relocate("com.github.puregero.multilib", "info.preva1l.fadlc.utils.multilib")
    relocate("dev.triumphteam.cmd", "info.preva1l.fadlc.commands.lib")
}