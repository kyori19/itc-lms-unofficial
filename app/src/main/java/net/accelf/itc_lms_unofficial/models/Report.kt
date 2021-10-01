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
        SUBMITTED_AFTER_DEADLINE(setOf("期限後提出", "Late submission")),
        TEMPORARILY_SAVED(setOf("一時保存", "Temporarily saved")),
        UNKNOWN(setOf()),
        ;

        companion object {
            fun String?.toReportStatus(): ReportStatus =
                values().firstOrNull { this in it.texts } ?: UNKNOWN
        }
    }

    companion object {
        val sample = Report(
            id = "99999",
            title = "Report Title",
            from = Date(),
            until = Date(),
            status = ReportStatus.SUBMITTED_IN_TIME,
        )
    }
}
