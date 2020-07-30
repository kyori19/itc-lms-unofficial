package net.accelf.itc_lms_unofficial.models

import java.io.Serializable
import java.util.*

val ATTENDANCE_ID_REGEX = Regex("""attendanceDetail\('\d+[A-Z]\d+,(\d+)'\);""")

data class Attendance(
    val id: String,
    val date: Date?,
    val status: AttendanceStatus
) : Serializable {
    enum class AttendanceStatus(private val texts: Set<String>) {
        PRESENT(setOf("出席", "Present")),
        ABSENT(setOf("欠席"));

        companion object {
            fun String.toAttendanceStatus(): AttendanceStatus {
                return values().first { this in it.texts }
            }
        }
    }
}
