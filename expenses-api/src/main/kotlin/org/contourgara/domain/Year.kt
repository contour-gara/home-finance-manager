package org.contourgara.domain

enum class Year(val intYear: Int) {
    _2026(intYear = 2026),
    _2027(intYear = 2027),
    ;

    companion object {
        fun of(intYear: Int): Year =
            entries.firstOrNull {
                it.intYear == intYear
            } ?: throw IllegalArgumentException("Not found: $intYear")
    }
}
