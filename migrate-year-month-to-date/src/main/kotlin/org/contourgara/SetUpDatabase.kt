package org.contourgara

import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database

fun setUpDatabase() {
    Flyway
        .configure()
//        .dataSource(
//            System.getenv("EXPENSES_API_MIGRATER_DATASOURCE_URL"),
//            System.getenv("DATASOURCE_USERNAME"),
//            System.getenv("DATASOURCE_PASSWORD"),
//        )
        .dataSource(
            "jdbc:mysql://localhost:3308/home_finance_manager_expenses_api_db",
            "user",
            "password",
        )
        .driver("com.mysql.cj.jdbc.Driver")
        .load()
        .migrate()

//    Database.connect(
//        url = "${System.getenv("EXPENSES_API_MIGRATER_DATASOURCE_URL")}",
//        driver = "com.mysql.cj.jdbc.Driver",
//        user = System.getenv("DATASOURCE_USERNAME"),
//        password = System.getenv("DATASOURCE_PASSWORD"),
//    )
    Database.connect(
        url = "jdbc:mysql://localhost:3308/home_finance_manager_expenses_api_db?rewriteBatchedStatements=TRUE",
        driver = "com.mysql.cj.jdbc.Driver",
        user = "user",
        password = "password",
    )
}
