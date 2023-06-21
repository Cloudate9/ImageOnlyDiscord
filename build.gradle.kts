plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.0-RC"
    kotlin("plugin.serialization") version "1.9.0-RC"
}

group = "com.cloudate9.imageonlydiscord"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.kord:kord-core:0.9.0")
    implementation("io.github.oshai:kotlin-logging-jvm:4.0.0-beta-29")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("org.slf4j:slf4j-simple:2.0.3")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "${rootProject.group}.DiscordBotKt"
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}