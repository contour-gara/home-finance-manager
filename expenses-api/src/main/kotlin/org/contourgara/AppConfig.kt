package org.contourgara

data object AppConfig {
    val datasourceUrl: String get() = System.getenv("EXPENSES_API_DATASOURCE_URL")
    val datasourceUser: String get() = System.getenv("DATASOURCE_USERNAME")
    val datasourcePassword: String get() = System.getenv("DATASOURCE_PASSWORD")
}
