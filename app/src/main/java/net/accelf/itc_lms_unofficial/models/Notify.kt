package net.accelf.itc_lms_unofficial.models

import java.io.Serializable
import java.util.*

val NOTIFY_ID_REGEX = Regex("""InfoDetailCourseTop\(event,(\d+)\);""")

data class Notify(
    val id: String,
    val title: String,
    val from: Date?,
    val until: Date?
) : Serializable
