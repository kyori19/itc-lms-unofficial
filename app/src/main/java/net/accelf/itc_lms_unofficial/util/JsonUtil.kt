package net.accelf.itc_lms_unofficial.util

import com.google.gson.Gson

inline fun <reified T> Gson.fromJson(json: String?): T {
    return fromJson(json ?: "", T::class.java)
}
