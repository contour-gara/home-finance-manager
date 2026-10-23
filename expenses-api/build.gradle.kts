import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.serialization)
    alias(libs.plugins.kover)
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
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ulid.kotlin)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.logging)
    implementation(libs.arrow.core)
    implementation(libs.exposed.core)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.exposed.jdbc)
    implementation(libs.flyway.mysql)
    runtimeOnly(libs.mysql.connector.j)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.extensions.ktor)
    testImplementation(libs.kotest.extensions.wiremock)
    testImplementation(libs.kotest.extensions.testcontainers)
    testImplementation(libs.kotest.assertions.arrow)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.testcontainers.mysql)
    testImplementation(libs.h2)
    testImplementation(libs.db.setup.kotlin)
    testImplementation(libs.assertj.db)
    testImplementation(libs.mockk)
}

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

val frontendDir = layout.projectDirectory.dir("frontend")

val installFrontend = tasks.register<Exec>("installFrontend") {
    description = "frontend の依存ライブラリを npm ci でインストールする"
    workingDir = frontendDir.asFile
    commandLine("npm", "ci")
    inputs.files(
        frontendDir.file("package.json"),
        frontendDir.file("package-lock.json"),
        frontendDir.file(".npmrc"),
    )
    outputs.file(frontendDir.file("node_modules/.package-lock.json"))
}

tasks.register<Exec>("buildFrontend") {
    description = "frontend をビルドして src/main/resources/frontend に出力する"
    workingDir = frontendDir.asFile
    commandLine("npm", "run", "build")
    inputs.files(installFrontend)
    inputs.dir(frontendDir.dir("src"))
    inputs.dir(frontendDir.dir("public"))
    inputs.files(
        frontendDir.file("index.html"),
        frontendDir.file("package.json"),
        frontendDir.file("tsconfig.json"),
        frontendDir.file("tsconfig.app.json"),
        frontendDir.file("tsconfig.node.json"),
        frontendDir.file("vite.config.ts"),
    )
    outputs.dir(layout.projectDirectory.dir("src/main/resources/frontend"))
}

tasks.processResources {
    mustRunAfter("buildFrontend")
}

tasks.run {
    dependsOn("buildFrontend")
}

tasks.build {
    dependsOn("buildFrontend")
}

ktor {
    fatJar {
        archiveFileName.set("${project.name}.jar")
    }
}

tasks.jar {
    archiveFileName.set("${project.name}-plain.jar")
}

tasks.shadowJar {
    mergeServiceFiles {
        include("META-INF/services/**")
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}

tasks.test {
    useJUnitPlatform()
    enableAssertions = false
    finalizedBy(tasks.koverHtmlReport)
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)

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
