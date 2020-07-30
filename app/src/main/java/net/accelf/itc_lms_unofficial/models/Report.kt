package net.accelf.itc_lms_unofficial.models

import java.io.Serializable
import java.util.*

data class Report(
    val id: String,
    val title: String,
    val from: Date?,
    val until: Date?,
    val status: ReportStatus
) : Serializable {
    enum class ReportStatus(val texts: Set<String>) {
        NOT_SUBMITTED(setOf("未提出", "Not Submitted")),
        SUBMITTED_IN_TIME(setOf("期限内提出", "Submit in time")),
        SUBMITTED_AFTER_DEADLINE(setOf("期限後提出", "Late submission"));

        companion object {
            fun fromSource(text: String): ReportStatus {
                return values().first { text in it.texts }
            }
        }
    }
}
