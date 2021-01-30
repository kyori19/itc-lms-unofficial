package net.accelf.itc_lms_unofficial.util

inline fun <reified T : Enum<*>> valueOf(value: String?): T? {
    return value?.let { enumValueOf<T>(it) }
}
