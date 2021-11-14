plugins {
    java
    kotlin("jvm") version "1.4.31"
    `kotlin-dsl`
    application
}

group = "org.xendv.java.edumail"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // annotations
    implementation ("org.jetbrains:annotations:13.0")
    implementation("org.projectlombok:lombok:1.18.4")
    annotationProcessor("org.projectlombok:lombok:1.18.4")

    // testing
    testImplementation("org.mockito:mockito-core:2.24.0")

    testImplementation("junit:junit:4.12")

    testImplementation("org.hamcrest:hamcrest-all:1.3")

    // db
    implementation("org.flywaydb:flyway-core:8.0.1")
    implementation("org.postgresql:postgresql:42.2.9")

    // json
    implementation("com.google.code.gson:gson:2.8.8")
}

tasks.jar {
    manifest {
        attributes["Main-Class"]="db.jdbc.MainApplication"
    }
}

task<JavaExec>("execute") {
    mainClass.set("db.jdbc.MainApplication")
    classpath = java.sourceSets["main"].runtimeClasspath
    standardInput = System.`in`
}