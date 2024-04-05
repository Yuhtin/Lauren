import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "com.yuhtin.lauren"
version = "3.0.0-BETA"

application {
    mainClass = "com.yuhtin.lauren.Startup"
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-beta.20") {
        exclude(module = "opus-java")
        exclude(group = "org.apache.logging.log4j")
    }

    implementation("com.google.guava:guava:31.0.1-jre")
    implementation("org.yaml:snakeyaml:2.2")
    implementation("io.github.cdimascio:dotenv-java:3.0.0")

    implementation("org.mongodb:mongodb-driver-sync:4.11.0")
    implementation("com.github.ben-manes.caffeine:caffeine:3.0.4")

    implementation("dev.arbjerg:lavaplayer:2.1.1")

    implementation("com.google.code.gson:gson:2.10.1")

    implementation("org.projectlombok:lombok:1.18.22")
    annotationProcessor("org.projectlombok:lombok:1.18.22")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    archiveFileName.set("bot.jar")

    println("Shadowing ${project.name} to ${destinationDirectory.get()}")
}
