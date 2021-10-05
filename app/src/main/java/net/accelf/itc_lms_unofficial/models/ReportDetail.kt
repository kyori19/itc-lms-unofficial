package net.accelf.itc_lms_unofficial.models

import net.accelf.itc_lms_unofficial.models.File.ScanStatus.Companion.toScanStatus
import net.accelf.itc_lms_unofficial.models.Report.ReportStatus
import net.accelf.itc_lms_unofficial.models.Report.ReportStatus.Companion.toReportStatus
import net.accelf.itc_lms_unofficial.network.DocumentConverterFactory
import net.accelf.itc_lms_unofficial.util.*
import okhttp3.ResponseBody
import java.io.Serializable
import java.util.*

data class ReportDetail(
    val id: String,
    val courseId: String,
    val title: String,
    val description: String,
    val attachmentFiles: List<File>,
    val from: Date?,
    val until: Date?,
    val afterDeadlineSubmittable: Boolean,
    val status: ReportStatus,
    val submissionType: SubmissionType,
    val submittedAt: Date?,
    val submittedFiles: List<SubmittedFile>,
    val submittedText: String,
    val fedBackBy: String,
    val feedbackComment: String,
    val fedBackAt: Date?,
    val feedbackScore: String,
    val feedbackFile: File?,
) : Serializable {

    enum class SubmissionType {
        FILE,
        TEXT_INPUT,
        NOT_ALLOWED,
    }

    class Converter(baseUri: String) :
        DocumentConverterFactory.DocumentConverter<ReportDetail>(baseUri) {

        override fun convert(value: ResponseBody): ReportDetail {
            document(value).let { document ->
                val details = document.select("#report_view .textareaContents")
                val times = document.select(".page_supple .label span")
                    .filter { TIME_SPAN_REGEX.matches(it.text()) }
                val feedback = document.select("#report_statu .subblock_form.break div")

                return ReportDetail(
                    document.select("#reportId").first()?.`val`() ?: "",
                    document.select("input[name=idnumber]").first()?.`val`() ?: "",
                    details.first()?.text() ?: "",
                    details.second().html(),
                    document.select(".page_supple .subblock_form div")
                        .filter { it.select("div .downloadFile").size == 1 }
                        .map {
                            File(
                                it.select(".objectName").first()?.text() ?: "",
                                it.select(".downloadFile").first()?.text() ?: "",
                                it.select(".scanStatus").first()?.text().toScanStatus(),
                            )
                        },
                    times.first().text().toDateTime(),
                    times.last().text().toDateTime(),
                    document.select(".page_supple .subblock_form")
                        .last()?.text() in listOf("可", "Allowed"),
                    // When there's no element of status, it could be assumed as not submitted.
                    // If there's an element and failed to parse into ReportStatus, it should be
                    // treated as UNKNOWN.
                    document.select("#report_statu .subblock_form span")
                        .firstOrNull()?.text()?.toReportStatus() ?: ReportStatus.NOT_SUBMITTED,
                    when {
                        document.select("#submissionArea").isNotEmpty() -> SubmissionType.FILE
                        document.select("#submissionText").isNotEmpty() -> SubmissionType.TEXT_INPUT
                        else -> SubmissionType.NOT_ALLOWED
                    },
                    document.select("#report_statu .subblock_form div")
                        .lastOrNull()?.text()?.toDateTime(),
                    document.select("#submissionFileResult .result_list_line").map { row ->
                        val cols = row.select(".result_list_txt span:not(.downloadLink)")

                        return@map SubmittedFile(
                            row.select("input[name=\"deleteFile\"]").getOrNull(0)?.`val`() ?: "",
                            cols.first()?.text() ?: "",
                            cols.last()?.text()?.toDateTime()!!,
                            File(
                                row.select(".objectName").first()?.text() ?: "",
                                row.select(".fileName").first()?.text() ?: "",
                                row.select(".scanStatus").first()?.text().toScanStatus(),
                            )
                        )
                    },
                    document.select("textarea#submissionText").firstOrNull()?.text() ?: "",
                    feedback.firstOrNull()?.text() ?: "",
                    feedback.secondOrNull()?.text() ?: "",
                    document.select("#report_statu .subblock_form:not(.break) div")
                        .takeIf { it.size > 2 }?.first()?.text()?.toDateTime(),
                    feedback.thirdOrNull()?.text() ?: "",
                    document.select("#report_statu .subblock_form.break .downloadFile")
                        .firstOrNull()?.let {
                            File(
                                document.select("#report_statu .subblock_form.break .objectName")
                                    .first()?.text() ?: "",
                                it.text(),
                                document.select("#report_statu .subblock_form.break .scanStatus")
                                    .first()?.text().toScanStatus(),
                            )
                        },
                )
            }
        }
    }

    companion object {
        val sample = ReportDetail(
            id = "99999",
            courseId = "2040111110Y01",
            title = "Report Title",
            description = "The description of the course will be displayed here.\nIt can be multi lines.",
            attachmentFiles = listOf(File.sample, File.sample),
            from = Date(),
            until = Date(),
            afterDeadlineSubmittable = false,
            status = ReportStatus.SUBMITTED_IN_TIME,
            submissionType = SubmissionType.FILE,
            submittedAt = Date(),
            submittedFiles = listOf(SubmittedFile.sample, SubmittedFile.sample),
            submittedText = "Answer",
            fedBackBy = "teacher name",
            feedbackComment = "very good!",
            fedBackAt = Date(),
            feedbackScore = "10",
            feedbackFile = File.sample,
        )
    }
}
