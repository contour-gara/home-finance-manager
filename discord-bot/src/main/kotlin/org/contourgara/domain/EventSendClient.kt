package org.contourgara.domain

interface EventSendClient {
    fun registerBill(bill: Bill)
}
