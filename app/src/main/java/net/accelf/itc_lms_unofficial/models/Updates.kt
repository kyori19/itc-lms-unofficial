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

private val COURSE_NAME_REGEX = Regex("""\[(Mon|Tue|Wed|Thu|Fri|Sat|[月火水木金土])([\d０-９])\s(.+)]""")

data class Updates(
    val updates: List<Update>,
    val throwable: Throwable?
) : Serializable {
    class UpdatesConverter(baseUrl: String) :
        DocumentConverterFactory.DocumentConverter<Updates>(baseUrl) {
        override fun convert(value: ResponseBody): Updates? {
            document(value).let { document ->
                return Updates(
                    document.select(".updateInfoTable .updateTableContents .updateInfoCell")
                        .map { row ->
                            lateinit var url: String
                            lateinit var courseName: String
                            lateinit var dow: TimeTable.DayOfWeek
                            var period = 0
                            lateinit var text: String
                            row.select(".message_link button").first().let { element ->
                                url = element.`val`()
                                COURSE_NAME_REGEX.matchEntire(element.select("span").first().text())
                                    ?.let {
                                        dow = it.groupValues[1].toDow()
                                        period = it.groupValues[2].toInt()
                                        courseName = it.groupValues[3]
                                    }
                                text = element.select("span").second().text()
                            }

                            Update(
                                row.select("input#updateInfoId").first().`val`(),
                                row.select("input#role").first().`val`().toRole(),
                                row.select(".message_link label").first().text().toDateTime(),
                                url,
                                row.select("input#idnumber").first().`val`(),
                                courseName,
                                dow,
                                period,
                                text,
                                row.select("input#contentId").first().`val`(),
                                row.select("input#module").first().`val`().toContentType(),
                                row.select("input#info_action").first().`val`().toActionType()
                            )
                        },
                    null
                )
            }
        }
    }
}
