package net.accelf.itc_lms_unofficial.models

import java.io.Serializable
import java.util.*

data class Notify(
    val id: String,
    val title: String,
    val from: Date?,
    val until: Date?,
) : Serializable {

    companion object {
        val sample = Notify(
            id = "75689",
            title = "Notify title",
            from = Date(),
            until = Date(),
        )

        val NOTIFY_ID_REGEX = Regex("""InfoDetailCourseTop\(event,(\d+)\);""")
    }
}
