dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
}

tasks.register("publishApi") {
    dependsOn("publishMavenJavaPublicationToFinallyADecentRepository")
}