import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kover)
    application
}

group = "org.contourgara"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.logback.classic)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.flyway.mysql)
    runtimeOnly(libs.mysql.connector.j)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.extensions.testcontainers)
    testImplementation(libs.testcontainers.mysql)
    testImplementation(libs.db.setup.kotlin)
    testImplementation(libs.assertj.db)
}

application {
    mainClass = "org.contourgara.MigrateYearDayToDateApplicationKt"
}

tasks.test {
    useJUnitPlatform()
    enableAssertions = false
    finalizedBy(tasks.koverHtmlReport)

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
