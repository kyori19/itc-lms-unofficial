package net.accelf.itc_lms_unofficial.models

import java.io.Serializable
import java.util.*

data class Attendance(
    val id: String,
    val date: Date?,
    val status: AttendanceStatus
) : Serializable {
    enum class AttendanceStatus(private val texts: Set<String>) {
        UNKNOWN(setOf()),
        PRESENT(setOf("出席", "Present")),
        LATE(setOf("遅刻", "Late")),
        ABSENT(setOf("欠席", "Absent")),
        ;

        companion object {
            fun String.toAttendanceStatus(): AttendanceStatus {
                return values().firstOrNull { this in it.texts } ?: UNKNOWN
            }
        }
    }

    companion object {
        val samplePresent = Attendance(
            id = "55555",
            date = Date(),
            status = AttendanceStatus.PRESENT,
        )
        val sampleLate = Attendance(
            id = "66666",
            date = Date(),
            status = AttendanceStatus.LATE,
        )
        val sampleAbsent = Attendance(
            id = "77777",
            date = Date(),
            status = AttendanceStatus.ABSENT,
        )

        val ATTENDANCE_ID_REGEX = Regex("""attendanceDetail\('\d+[A-Z]\d+,(\d+)'\);""")
    }
}
