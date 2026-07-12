package org.contourgara

import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database

fun setUpDatabase() {
    Flyway
        .configure()
        .dataSource(
            System.getenv("EXPENSES_API_DATASOURCE_URL"),
            System.getenv("DATASOURCE_USERNAME"),
            System.getenv("DATASOURCE_PASSWORD"),
        )
        .driver("com.mysql.cj.jdbc.Driver")
        .load()
        .migrate()

    Database.connect(
        url = System.getenv("EXPENSES_API_DATASOURCE_URL"),
        driver = "com.mysql.cj.jdbc.Driver",
        user = System.getenv("DATASOURCE_USERNAME"),
        password = System.getenv("DATASOURCE_PASSWORD"),
    )
}
