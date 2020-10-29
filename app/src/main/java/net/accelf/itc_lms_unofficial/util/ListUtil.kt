package net.accelf.itc_lms_unofficial.util

fun <T> MutableList<T>.popIf(predicate: (T?) -> Boolean): T? {
    return if (predicate(firstOrNull())) removeAt(0) else null
}
