import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    jacoco
    application
}

group = "org.contourgara"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.ulid.kotlin)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    runtimeOnly(libs.mysql.connector.j)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.extensions.ktor)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.testcontainers.mysql)
    testImplementation(libs.database.rider.core)
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass = "org.contourgara.UlidSequencerApplicationKt"
}

ktor {
    fatJar {
        archiveFileName.set("${project.name}.jar")
    }
}

tasks.jar {
    archiveFileName.set("${project.name}-plain.jar")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
    jvmArgs("--add-opens=java.base/java.util=ALL-UNNAMED")

    testLogging {
        events = setOf(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED,
            TestLogEvent.STANDARD_OUT
        )

        exceptionFormat = TestExceptionFormat.FULL
        showStandardStreams = true
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.required.set(true)
    }
}
