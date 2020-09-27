package net.accelf.itc_lms_unofficial.models

import com.google.gson.Gson
import net.accelf.itc_lms_unofficial.models.Attendance.AttendanceStatus.Companion.toAttendanceStatus
import net.accelf.itc_lms_unofficial.models.Message.MessageStatus.Companion.toMessageStatus
import net.accelf.itc_lms_unofficial.models.QuillData.Companion.parseQuill
import net.accelf.itc_lms_unofficial.models.TimeTable.DayOfWeek.Companion.toDow
import net.accelf.itc_lms_unofficial.network.DocumentConverterFactory
import net.accelf.itc_lms_unofficial.util.*
import okhttp3.ResponseBody
import java.io.Serializable
import java.util.*

private val COURSE_NAME_REGEX = Regex("""(.+)\s(\d+)\s(.+)""")
private val PERIOD_REGEX = Regex("""([^,/]+)/[^,/]*([\d０-９他]|Other)[^,/]*""")
private val SEMESTER_REGEX =
    Regex("""([^,/]+)/([^,/]+/[^,/]*(?:[\d０-９他]|Other)[^,/]*(?:,[^,/]+/[^,/]*(?:[\d０-９他]|Other)[^,/]*)*)""")

data class CourseDetail(
    val id: String,
    val department: String,
    val courseCode: String,
    val name: String,
    val teachers: List<String>,
    val semester: String,
    val periods: List<Pair<TimeTable.DayOfWeek, Int>>,
    val summary: String,
    val onlineInfoUpdatedAt: Date?,
    val onlineInfo: QuillData?,
    val notifies: List<Notify>,
    val courseContents: List<CourseContent>,
    val reports: List<Report>,
    val messages: List<Message>,
    val attendances: List<Attendance>,
    val tests: List<Test>,
    val forums: List<Forum>,
    val surveys: List<Survey>,
) : Serializable {

    class Converter(baseUrl: String, private val gson: Gson) :
        DocumentConverterFactory.DocumentConverter<CourseDetail>(baseUrl) {
        override fun convert(value: ResponseBody): CourseDetail? {
            document(value).let { document ->
                var (department, courseCode, name) = Triple("", "", "")
                COURSE_NAME_REGEX.matchEntire(
                    document.select("#courseName .page_name_txt")
                        .first().text()
                )?.let {
                    department = it.groupValues[1]
                    courseCode = it.groupValues[2]
                    name = it.groupValues[3]
                }

                var teachers: List<String>
                var semester = ""
                val periods = mutableListOf<Pair<TimeTable.DayOfWeek, Int>>()
                document.select("#syllabusSupplement .page_supple_title").let { syllabus ->
                    teachers = syllabus.first().select("span").last().text().split(",")
                    SEMESTER_REGEX.matchEntire(
                        syllabus.second().select(".subblock_form")
                            .first().text()
                    )?.let {
                        semester = it.groupValues[1]
                        it.groupValues[2].split(",").forEach { periodStr ->
                            PERIOD_REGEX.matchEntire(periodStr)?.let { result ->
                                periods.add(result.groupValues[1].toDow() to (result.groupValues[2].toIntOrNull()
                                    ?: -1))
                            }
                        }
                    }
                }

                return CourseDetail(
                    document.select("input[name=\"idnumber\"]").first().`val`(),
                    department,
                    courseCode,
                    name,
                    teachers,
                    semester,
                    periods,
                    document.select("#syllabusSupplement .page_supple_txt p").first().html(),
                    document.select("#syllabusSupplement .page_online_date").firstOrNull()?.text()
                        ?.substringAfter(":")?.toDateTime(),
                    document.select("#courseTopForm input#onlineText").first().`val`()
                        .parseQuill(gson),
                    document.select("#information .subblock_list_line").map { row ->
                        var (id, title) = Pair("", "")
                        row.select(".subblock_list_txt1 a").first()?.let {
                            id =
                                NOTIFY_ID_REGEX.matchEntire(it.attr("onclick"))?.groupValues?.get(1)
                                    ?: ""
                            title = it.text()
                        }

                        val times = row.select(".subblock_list_txt2").first().text().toTimeSpan()

                        Notify(
                            id,
                            title,
                            times[0],
                            times[1]
                        )
                    },
                    document.select(
                        "#materialList .subblock_list_head," +
                                "#materialList .subblock_line," +
                                "#materialList .subblock_list_comment," +
                                "#materialList .material_list_line"
                    )
                        .let { row ->
                            val courseContents = mutableListOf<CourseContent>()
                            var builder: CourseContent.Builder? = null
                            row.forEach {
                                when {
                                    it.hasClass("subblock_list_head") -> {
                                        if (builder != null) {
                                            courseContents.add(builder!!.build())
                                        }
                                        builder = CourseContent.Builder()
                                        builder!!.title = it.select("label").first().text()
                                    }
                                    it.hasClass("subblock_line") -> {
                                        val times =
                                            it.select(".subblock_form").first().text().toTimeSpan()
                                        builder!!.from = times[0]
                                        builder!!.until = times[1]
                                    }
                                    it.hasClass("subblock_list_comment") -> {
                                        builder!!.summary = it.html()
                                    }
                                    it.hasClass("material_list_line") -> {
                                        val type = when {
                                            it.select(".fileDownload")
                                                .isNotEmpty() -> Material.MaterialType.FILE
                                            it.select("a")
                                                .isNotEmpty() -> Material.MaterialType.LINK
                                            else -> Material.MaterialType.VIDEO
                                        }
                                        builder!!.materials.add(
                                            Material(
                                                it.select(".resource_Id").first().text(),
                                                it.select("#dlMaterialId").first().`val`(),
                                                type,
                                                when (type) {
                                                    Material.MaterialType.FILE -> it.select("label.fileDownload")
                                                        .first().text()
                                                    Material.MaterialType.LINK -> it.select("a")
                                                        .first().text()
                                                    else -> ""
                                                },
                                                when (type) {
                                                    Material.MaterialType.FILE -> File(
                                                        it.select(".objectName").first().text(),
                                                        it.select(".fileName").first().text(),
                                                        File.ScanStatus.fromText(it.select(".scanStatus")
                                                            .first().text())
                                                    )
                                                    else -> null
                                                },
                                                when (type) {
                                                    Material.MaterialType.LINK -> it.select("a")
                                                        .first().attr("href")
                                                    else -> null
                                                },
                                                it.select(".result_list_txt").last().text()
                                                    .toDate(),
                                                it.select(".openEndDate").first().text()
                                                    .toTimeSeconds()
                                            )
                                        )
                                    }
                                }
                            }
                            if (builder != null) {
                                courseContents.add(builder!!.build())
                            }
                            courseContents
                        },
                    document.select("#reportList .report_list_line").map { row ->
                        Report(
                            row.select("input.reportId").first().`val`(),
                            row.select(".result_list_txt.break a").first().text(),
                            TIME_FORMAT.parse(
                                row.select(".result_list_txt.timeStart").first().text()
                            ),
                            TIME_FORMAT.parse(
                                row.select(".result_list_txt.timeEnd").first().text()
                            ),
                            Report.ReportStatus.fromSource(
                                row.select(".result_list_txt.submitStatus label").first().text()
                            )
                        )
                    },
                    document.select("#message #inquiryList .result_list_line").map { row ->
                        lateinit var id: String
                        lateinit var title: String
                        row.select(".result_list_txt a").first().let {
                            id = it.attr("href").getMessageId()
                            title = it.text()
                        }

                        var (status, by) = Pair<Message.MessageStatus, String?>(
                            Message.MessageStatus.COMPLETED,
                            null
                        )
                        MESSAGE_STATUS_REGEX.matchEntire(
                            row.select(".inquiryStatus")
                                .first().text()
                        )?.let {
                            status = it.groupValues[1].toMessageStatus()
                            by = it.groupValues[2]
                        }

                        Message(
                            id,
                            title,
                            row.select(".createDate").first().text().toDateTime(),
                            status,
                            when (status) {
                                Message.MessageStatus.COMPLETED -> by
                                else -> null
                            },
                            when (status) {
                                Message.MessageStatus.COMPLETED -> null
                                else -> by!!.toDate()
                            }
                        )
                    },
                    document.select("#attendance .result_list_line").map { row ->
                        lateinit var id: String
                        lateinit var status: Attendance.AttendanceStatus
                        row.select(".result_list_txt").last().let {
                            id = ATTENDANCE_ID_REGEX.matchEntire(it.attr("onclick"))
                                ?.groupValues?.get(1) ?: ""
                            status = it.text().toAttendanceStatus()
                        }

                        Attendance(
                            id,
                            row.select(".result_list_txt").first().text().toDate(),
                            status
                        )
                    },
                    document.select("#examination #examinationList .result_list_line").map { row ->
                        var id: String? = null
                        lateinit var title: String
                        var status = Test.TestStatus.UNKNOWN
                        row.select(
                            ".result_list_txt.break a," +
                                    ".result_list_txt.break label"
                        )
                            .first().let { element ->
                                if (element.tagName() == "a") {
                                    element.attr("href").getTestParams().let {
                                        id = it.first
                                        status = it.second
                                    }
                                }
                                title = element.text()
                            }

                        val times = row.select(".result_list_txt").second().text().toTimeSpan()

                        Test(
                            id,
                            title,
                            status,
                            times[0],
                            times[1]
                        )
                    },
                    document.select("#discussion #forumList .result_list_line").map { row ->
                        lateinit var id: String
                        lateinit var title: String
                        row.select(".result_list_txt.break a").first().let { element ->
                            id = element.attr("href").getForumId()
                            title = element.text()
                        }

                        val times = row.select(".result_list_txt").second().text().toTimeSpan()

                        Forum(
                            id,
                            title,
                            times[0],
                            times[1]
                        )
                    },
                    document.select("#questionnaire #surveyList .result_list_line").map { row ->
                        lateinit var id: String
                        lateinit var title: String
                        row.select(".result_list_txt.break a").first().let { element ->
                            id = element.attr("href").getSurveyId()
                            title = element.text()
                        }

                        val times = row.select(".result_list_txt").second().text().toTimeSpan()

                        Survey(
                            id,
                            title,
                            times[0],
                            times[1]
                        )
                    }
                )
            }
        }
    }
}
