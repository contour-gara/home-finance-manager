package org.contourgara

import io.ktor.server.config.ApplicationConfig

@ConsistentCopyVisibility
data class AppConfig private constructor (
    val datasourceUrl: String,
    val datasourceUser: String,
    val datasourcePassword: String,
) {
    companion object {
        fun from(applicationConfig: ApplicationConfig): AppConfig =
            AppConfig(
                datasourceUrl = applicationConfig.property("application.datasource.url").getString(),
                datasourceUser = applicationConfig.property("application.datasource.username").getString(),
                datasourcePassword = applicationConfig.property("application.datasource.password").getString(),
            )
    }
}
