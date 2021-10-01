package net.accelf.itc_lms_unofficial.models

import java.io.Serializable
import java.util.*

data class Update(
    val id: String,
    val role: Role,
    val createdAt: Date?,
    val url: String,
    val courseId: String,
    val courseName: String,
    val periods: List<Pair<TimeTable.DayOfWeek, Int>>,
    val text: String,
    val contentId: String,
    val contentType: ContentType,
    val actionType: ActionType,
) : Serializable {

    val targetId = "${role.idPrefix}-${id}"

    enum class Role(val idPrefix: Int, private val text: String) {
        STUDENT(2, "STUDENT"),
        UNKNOWN(-1, ""),
        ;

        companion object {
            fun String?.toRole(): Role =
                values().firstOrNull { this == it.text } ?: UNKNOWN
        }
    }

    enum class ContentType(private val text: String) {
        UNKNOWN("unknown"),
        FORUM("theme"),
        MATERIAL("material"),
        NOTIFY("information"),
        ONLINE_INFO("online_course"),
        REPORT("report"),
        THREAD("thread"),
        ;

        companion object {
            fun String?.toContentType(): ContentType =
                values().firstOrNull { this == it.text } ?: UNKNOWN
        }
    }

    enum class ActionType(private val text: String) {
        UNKNOWN("unknown"),
        ADD("add"),
        POST("post"),
        SUBMIT("submit"),
        UPDATE("update"),
        ;

        companion object {
            fun String?.toActionType(): ActionType =
                values().firstOrNull { this == it.text } ?: UNKNOWN
        }
    }
}
