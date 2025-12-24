package org.contourgara.domain

enum class Month(val intMonth: Int) {
    JANUARY(intMonth = 1),
    FEBRUARY(intMonth = 2),
    MARCH(intMonth = 3),
    APRIL(intMonth = 4),
    MAY(intMonth = 5),
    JUNE(intMonth = 6),
    JULY(intMonth = 7),
    AUGUST(intMonth = 8),
    SEPTEMBER(intMonth = 9),
    OCTOBER(intMonth = 10),
    NOVEMBER(intMonth = 11),
    DECEMBER(intMonth = 12),
    ;

    companion object {
        fun of(intMonth: Int): Month =
            entries.firstOrNull {
                it.intMonth == intMonth
            } ?: throw IllegalArgumentException("Not found: $intMonth")
    }
}
