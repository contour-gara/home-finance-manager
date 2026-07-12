package org.contourgara.domain

data class OldExpense(
    val id: String,
    val year: Int,
    val month: Int,
    val memo: String,
) {
    // 採用
    fun haveyyyyMMdd(): Boolean =
        memo.contains(regex = Regex(pattern = """(?<!\d)(\d{4})(\d{2})(\d{2})(?!\d)"""))

    // 最初の日付を採用する
    fun haveSlashes(): Boolean =
        memo.indexOfFirst { it == '/'} != memo.indexOfLast { it == '/'}

    // 採用
    fun haveSlash(): Boolean = memo.contains(char = '/')
}

// 日付無しは 1 日扱い OK
// 1/5,6,7,9のお昼ご飯代(社食)
// TODO: Month と Day を抽出したら month と差があるか確認
