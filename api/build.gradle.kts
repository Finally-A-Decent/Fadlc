val minecraftVersion: String by project

dependencies {
    compileOnly("io.papermc.paper:paper-api:$minecraftVersion")
}

tasks.register("publishApi") {
    dependsOn("publishMavenJavaPublicationToFinallyADecentRepository")
}