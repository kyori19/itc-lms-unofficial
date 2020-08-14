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
    val dow: TimeTable.DayOfWeek,
    val period: Int,
    val text: String,
    val contentId: String,
    val contentType: ContentType,
    val actionType: ActionType
) : Serializable {

    val targetId = "${role.idPrefix}-${id}"

    enum class Role(val idPrefix: Int, private val text: String) {
        STUDENT(2, "STUDENT");

        companion object {
            fun String.toRole(): Role {
                return values().first { this == it.text }
            }
        }
    }

    enum class ContentType(private val text: String) {
        NOTIFY("information"),
        REPORT("report");

        companion object {
            fun String.toContentType(): ContentType {
                return values().first { this == it.text }
            }
        }
    }

    enum class ActionType(private val text: String) {
        ADD("add"),
        SUBMIT("submit"),
        UPDATE("update");

        companion object {
            fun String.toActionType(): ActionType {
                return values().first { this == it.text }
            }
        }
    }
}
