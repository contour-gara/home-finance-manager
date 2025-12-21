package org.contourgara

import kotlinx.serialization.Serializable
import ulid.ULID

@Serializable
data class Expense(
    val id: String,
    val amount: Int,
)
