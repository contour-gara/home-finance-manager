package org.contourgara.eventlistener

fun String.parseAmount(): String =
    trim().replace(",", "")
        .removeSuffix(" 円")
