package net.accelf.itc_lms_unofficial.models

import net.accelf.itc_lms_unofficial.network.DocumentConverterFactory
import net.accelf.itc_lms_unofficial.util.popIf
import net.accelf.itc_lms_unofficial.util.toTimeSpan
import okhttp3.ResponseBody
import java.io.Serializable
import java.util.*

data class AttendanceSend(
    val id: String,
    val csrf: String,
    val sent: Boolean,
    val allowedSince: Date?,
    val allowedUntil: Date?,
    val lateSince: Date?,
    val lateUntil: Date?,
    val password: String,
    val errorOnPassword: String,
    val comment: String,
    val errorOnComment: String,
    val success: Boolean,
) : Serializable {

    class Converter(baseUrl: String) :
        DocumentConverterFactory.DocumentConverter<AttendanceSend>(baseUrl) {

        override fun convert(value: ResponseBody): AttendanceSend {
            document(value).let { document ->
                document.select("#attendancesSendForm").first().let { form ->
                    var allowedSince: Date? = null
                    var allowedUntil: Date? = null
                    var lateSince: Date? = null
                    var lateUntil: Date? = null
                    form?.select(".subblock_contents .subblock_line .subblock_form .important")
                        ?.apply {
                            first()?.text()?.toTimeSpan()?.apply {
                                allowedSince = get(0)
                                allowedUntil = get(1)
                            }
                            last()?.text()?.toTimeSpan()?.apply {
                                lateSince = get(0)
                                lateUntil = get(1)
                            }
                        }

                    val passInput = form?.select("input[name=oneTimePass]")?.first()
                    val commentTextArea = form?.select("textarea[name=comment]")?.first()
                    val errors =
                        form?.select(".subblock_form .errorMsg")?.map { it.text() }?.toMutableList()

                    return AttendanceSend(
                        form?.select("input[name=attendanceId]")?.first()?.`val`() ?: "",
                        form?.select("input[name=_csrf]")?.first()?.`val`() ?: "",
                        (form?.select("input[name=isSent]")?.first()?.`val`() ?: "true") == "true",
                        allowedSince,
                        allowedUntil,
                        lateSince,
                        lateUntil,
                        passInput?.`val`() ?: "",
                        errors?.popIf { passInput?.hasClass("inputErrorField") ?: false } ?: "",
                        commentTextArea?.text() ?: "",
                        errors?.popIf { commentTextArea?.hasClass("inputErrorField") ?: false }
                            ?: "",
                        document.select("#attendanceSendComplete").isNotEmpty(),
                    )
                }
            }
        }
    }

    companion object {
        val sample = AttendanceSend(
            id = "55555",
            csrf = "",
            sent = true,
            allowedSince = Date(),
            allowedUntil = Date(),
            lateSince = Date(),
            lateUntil = Date(),
            password = "foo",
            errorOnPassword = "Wrong password",
            comment = "",
            errorOnComment = "",
            success = false,
        )
    }
}
