package net.accelf.itc_lms_unofficial.models

import net.accelf.itc_lms_unofficial.models.TimeTable.DayOfWeek.Companion.toDow
import net.accelf.itc_lms_unofficial.models.Update.ActionType.Companion.toActionType
import net.accelf.itc_lms_unofficial.models.Update.ContentType.Companion.toContentType
import net.accelf.itc_lms_unofficial.models.Update.Role.Companion.toRole
import net.accelf.itc_lms_unofficial.network.DocumentConverterFactory
import net.accelf.itc_lms_unofficial.util.second
import net.accelf.itc_lms_unofficial.util.toDateTime
import okhttp3.ResponseBody
import java.io.Serializable

data class Updates(
    val csrf: String,
    val updates: List<Update>,
    val throwable: Throwable?,
) : Serializable {

    class Converter(baseUrl: String) :
        DocumentConverterFactory.DocumentConverter<Updates>(baseUrl) {
        override fun convert(value: ResponseBody): Updates {
            document(value).let { document ->
                return Updates(
                    document.select("input[name=_csrf]").first()?.`val`() ?: "",
                    document.select(".updateInfoTable .updateTableContents .updateInfoCell")
                        .map { row ->
                            lateinit var url: String
                            lateinit var courseName: String
                            val periods = mutableListOf<Pair<TimeTable.DayOfWeek, Int>>()
                            lateinit var text: String
                            row.select(".message_link button").first()?.let { element ->
                                url = element.`val`()
                                element.select("span").first()?.text()
                                    ?.let { COURSE_NAME_REGEX.matchEntire(it) }
                                    ?.let {
                                        it.groupValues[1].split("・").forEach { periodText ->
                                            PERIOD_REGEX.matchEntire(periodText)?.let { result ->
                                                periods.add(result.groupValues[1].toDow() to (result.groupValues[2].toIntOrNull()
                                                    ?: -1))
                                            }
                                        }
                                        courseName = it.groupValues[2]
                                    }
                                text = element.select("span").second().text()
                            }

                            Update(
                                row.select("input#updateInfoId").first()?.`val`() ?: "",
                                row.select("input#role").first()?.`val`().toRole(),
                                row.select(".message_link label").first()?.text()?.toDateTime(),
                                url,
                                row.select("input#idnumber").first()?.`val`() ?: "",
                                courseName,
                                periods,
                                text,
                                row.select("input#contentId").first()?.`val`() ?: "",
                                row.select("input#module").first()?.`val`().toContentType(),
                                row.select("input#info_action").first()?.`val`().toActionType(),
                            )
                        },
                    null
                )
            }
        }
    }

    companion object {
        private val PERIOD_REGEX =
            Regex("""(Mon|Tue|Wed|Thu|Fri|Sat|Other|[月火水木金土他])([\d０-９他]|Other)""")
        private val COURSE_NAME_REGEX =
            Regex("""\[((?:Mon|Tue|Wed|Thu|Fri|Sat|Other|[月火水木金土他])(?:[\d０-９他]|Other)(?:・(?:Mon|Tue|Wed|Thu|Fri|Sat|Other|[月火水木金土他])(?:[\d０-９他]|Other))*)\s(.+)]""")
    }
}
