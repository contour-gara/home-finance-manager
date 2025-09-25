import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
    jacoco
    application
}

group = "org.contourgara"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kord.core)
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
    ksp(libs.koin.ksp.compiler)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.logging)
    implementation(libs.kotlin.logging)
    implementation(libs.slf4j.simple)
    implementation(libs.arrow.core)
    implementation(libs.ulid.kotlin)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.extensions.koin)
    testImplementation(libs.kotest.extensions.wiremock)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.assertions.arrow)
    testImplementation(libs.koin.test)
    testImplementation(libs.mockk)
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass = "org.contourgara.DiscordBotApplicationKt"
}

tasks.test {
    useJUnitPlatform()

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

    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.required.set(true)
    }
}

tasks.withType<Jar> {
    manifest { attributes["Main-Class"] = "org.contourgara.DiscordBotApplicationKt" }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    configurations.compileClasspath.get().forEach {
        from(if (it.isDirectory) it else zipTree(it))
    }
}
