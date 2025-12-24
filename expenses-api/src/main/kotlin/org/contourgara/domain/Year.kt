package org.contourgara.domain

enum class Year(private val intYear: Int) {
    _2026(intYear = 2026),
    ;

    companion object {
        fun of(intYear: Int): Year =
            entries.firstOrNull {
                it.intYear == intYear
            } ?: throw IllegalArgumentException("Not found: $intYear")
    }
}
