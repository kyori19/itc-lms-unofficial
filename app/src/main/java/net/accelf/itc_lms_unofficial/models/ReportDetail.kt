package net.accelf.itc_lms_unofficial.models

import net.accelf.itc_lms_unofficial.network.DocumentConverterFactory
import net.accelf.itc_lms_unofficial.util.second
import net.accelf.itc_lms_unofficial.util.secondOrNull
import net.accelf.itc_lms_unofficial.util.thirdOrNull
import net.accelf.itc_lms_unofficial.util.toDateTime
import okhttp3.ResponseBody
import java.io.Serializable
import java.util.*

data class ReportDetail(
    val id: String,
    val courseId: String,
    val title: String,
    val description: String,
    val attachmentFile: File?,
    val from: Date?,
    val until: Date?,
    val afterDeadlineSubmittable: Boolean,
    val status: Report.ReportStatus,
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

        override fun convert(value: ResponseBody): ReportDetail? {
            document(value).let { document ->
                val details = document.select("#report_view .textareaContents")
                val times = document.select(".page_supple .label span")
                val feedback = document.select("#report_statu .subblock_form.break div")

                return ReportDetail(
                    document.select("#reportId").first().`val`(),
                    document.select("input[name=idnumber]").first().`val`(),
                    details.first().text(),
                    details.second().html(),
                    document.select(".page_supple .downloadFile").firstOrNull()?.let {
                        File(
                            document.select(".page_supple .objectName").first().text(),
                            it.text(),
                            File.ScanStatus.fromText(document.select(".page_supple .scanStatus")
                                .first().text())
                        )
                    },
                    times.first().text().toDateTime(),
                    times.last().text().toDateTime(),
                    document.select(".page_supple .subblock_form").last().text() in listOf("可",
                        "Allowed"),
                    document.select("#report_statu .subblock_form span")
                        .firstOrNull()?.let {
                            Report.ReportStatus.fromSource(it.text())
                        } ?: Report.ReportStatus.NOT_SUBMITTED,
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
                            cols.first().text(),
                            cols.last().text().toDateTime()!!,
                            File(
                                row.select(".objectName").first().text(),
                                row.select(".fileName").first().text(),
                                File.ScanStatus.fromText(row.select(".scanStatus").first().text())
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
                                    .first().text(),
                                it.text(),
                                File.ScanStatus.fromText(document.select("#report_statu .subblock_form.break .scanStatus")
                                    .first().text()),
                            )
                        },
                )
            }
        }
    }
}
