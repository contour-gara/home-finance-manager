package org.contourgara.eventlistener

fun Int.formatAmount(): String =
    "${toString()
        .reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
    } 円"
