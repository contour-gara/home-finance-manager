package org.contourgara.domain

fun interface EventSendClient {
    fun execute(billOperation: BillOperation, bill: Bill)
}
