package org.contourgara.eventlistener

enum class User(val id: Long) {
    GARA(703805458116509818),
    YUKI(889339009061507143),
    UNDEFINED(0);

    companion object {
        fun of (id: Long) = entries.find { it.id == id }?: UNDEFINED
        fun of(name: String): User = entries.find { it.name == name.uppercase() }?: UNDEFINED
    }
}
