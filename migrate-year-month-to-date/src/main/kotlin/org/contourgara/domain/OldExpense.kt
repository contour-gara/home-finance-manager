package org.contourgara.domain

data class OldExpense(
    val id: String,
    val year: Int,
    val month: Int,
    val memo: String,
) {
    // 最初の日付を採用する
    fun haveSlashes(): Boolean {
        return memo.indexOfFirst { it == '/'} != memo.indexOfLast { it == '/'}
    }
}
