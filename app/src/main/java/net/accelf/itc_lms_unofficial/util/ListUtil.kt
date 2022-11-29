package net.accelf.itc_lms_unofficial.util

fun <T> MutableList<T>.popIf(predicate: (T?) -> Boolean): T? {
    return if (predicate(firstOrNull())) removeAt(0) else null
}

fun <T> List<T>.second(): T {
    return secondOrNull()!!
}

fun <T> List<T>.secondOrNull(): T? {
    return if (size >= 2) get(1) else null
}

fun <T> List<T>.thirdOrNull(): T? {
    return if (size >= 3) get(2) else null
}
