package net.accelf.itc_lms_unofficial.models

import net.accelf.itc_lms_unofficial.network.DocumentConverterFactory
import okhttp3.ResponseBody
import java.io.Serializable

data class TimeTable(
    val name: String,
    val courses: List<List<Course?>>
) : Serializable {
    enum class DayOfWeek(val index: Int, val texts: Set<String>) {
        MON(0, setOf("Monday", "月曜日")),
        TUE(1, setOf("Tuesday", "火曜日")),
        WED(2, setOf("Wednesday", "水曜日")),
        THU(3, setOf("Thursday", "木曜日")),
        FRI(4, setOf("Friday", "金曜日")),
        SAT(5, setOf("Saturday", "土曜日"));

        companion object {
            fun fromIndex(index: Int): DayOfWeek {
                return values().first { it.index == index }
            }

            fun String.toDow(): DayOfWeek {
                return values().first { this in it.texts }
            }
        }
    }

    class TimeTableConverter(baseUrl: String) :
        DocumentConverterFactory.DocumentConverter<TimeTable>(baseUrl) {
        override fun convert(value: ResponseBody): TimeTable? {
            document(value).let {
                return TimeTable(
                    it.select("#page_contents .login_view_name").first().text(),
                    it.select("#page_contents .divTable:not(.otherCourse) .divTableBody .divTableRow.data")
                        .map { period ->
                            period.select(".divTableCell").map { cell ->
                                cell.select(".divTableCellRow").first()?.let { row ->
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
                            DayOfWeek.values().map { dow ->
                                list.map { period ->
                                    period[dow.index]
                                }
                            }
                        }
                )
            }
        }
    }
}
