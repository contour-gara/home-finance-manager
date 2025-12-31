package org.contourgara.domain

@JvmInline
value class Amount(val value: Int) {
    operator fun plus(other: Amount): Amount = Amount(value + other.value)
}
