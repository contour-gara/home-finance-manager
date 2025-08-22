package org.contourgara.domain

enum class User {
    GARA,
    YUKI;

    companion object {
        fun of(userName: String): User = entries.find { it.lowercaseName() == userName } ?: throw RuntimeException("ユーザーが存在しない: $userName")
    }

    fun lowercaseName(): String = name.lowercase()
}
