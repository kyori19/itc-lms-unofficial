package net.accelf.itc_lms_unofficial.models

import net.accelf.itc_lms_unofficial.models.SelectOption.Companion.toSelectOptions
import net.accelf.itc_lms_unofficial.network.DocumentConverterFactory
import net.accelf.itc_lms_unofficial.util.toDate
import okhttp3.ResponseBody
import java.io.Serializable
import java.util.*

private val DATE_OR_DATE_SPAN_REGEX = Regex("""([^～]+)(?:\s～\s([^～]+))?""")

data class TimeTable(
    val name: String,
    val courses: List<List<List<Course>>>,
    val years: List<SelectOption>,
    val terms: List<SelectOption>,
    val since: Date,
    val until: Date?,
) : Serializable {
    enum class DayOfWeek(val index: Int, val texts: Set<String>) {
        OTHER(-1, setOf("Other", "その他", "Other", "他")),
        MON(0, setOf("Monday", "月曜日", "Mon", "月")),
        TUE(1, setOf("Tuesday", "火曜日", "Tue", "火")),
        WED(2, setOf("Wednesday", "水曜日", "Wed", "水")),
        THU(3, setOf("Thursday", "木曜日", "Thu", "木")),
        FRI(4, setOf("Friday", "金曜日", "Fri", "金")),
        SAT(5, setOf("Saturday", "土曜日", "Sat", "土")),
        ;

        companion object {
            fun week(): List<DayOfWeek> {
                return values().filter { it.index > -1 }
            }

            fun fromIndex(index: Int): DayOfWeek {
                return values().first { it.index == index }
            }

            fun String.toDow(): DayOfWeek {
                return values().first { this in it.texts }
            }
        }
    }

    class Converter(baseUrl: String) :
        DocumentConverterFactory.DocumentConverter<TimeTable>(baseUrl) {
        override fun convert(value: ResponseBody): TimeTable? {
            document(value).let {
                lateinit var since: Date
                var until: Date? = null
                it.select(".selectedDisplayDate").firstOrNull()?.text()?.let { dateStr ->
                    DATE_OR_DATE_SPAN_REGEX.matchEntire(dateStr)?.let { result ->
                        since = result.groupValues[1].toDate()!!
                        until = result.groupValues[2].toDate()
                    }
                }

                return TimeTable(
                    it.select("#page_contents .login_view_name").first().text(),
                    it.select("#page_contents .divTable:not(.otherCourse) .divTableBody .divTableRow.data")
                        .map { period ->
                            period.select(".divTableCell").map { cell ->
                                cell.select(".divTableCellRow").map { row ->
                                    row.select(".divTableCellHeader").first().let { header ->
                                        Course(
                                            header.id(),
                                            header.text(),
                                            row.select(".divTableCellTeacher").first().children()
                                                .map { span ->
                                                    span.text().replace(", ", "")
                                                },
                                            row.select(".enrolTemp_icon").isNotEmpty()
                                        )
                                    }
                                }
                            }
                        }.let { list ->
                            DayOfWeek.week().map { dow ->
                                list.map { period ->
                                    period[dow.index]
                                }
                            }
                        },
                    it.select(".condition_select#nendo option").toSelectOptions(),
                    it.select(".condition_select.term:not([disabled]) option").toSelectOptions(),
                    since,
                    until,
                )
            }
        }
    }
}
