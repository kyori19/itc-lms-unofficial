package net.accelf.itc_lms_unofficial.models

import net.accelf.itc_lms_unofficial.network.DocumentConverterFactory
import okhttp3.ResponseBody
import java.io.Serializable

data class TimeTable(
    val name: String,
    val courses: List<List<Course?>>
) : Serializable {
    enum class DayOfWeek(val index: Int) {
        MON(0),
        TUE(1),
        WED(2),
        THU(3),
        FRI(4),
        SAT(5);

        companion object {
            fun fromIndex(index: Int): DayOfWeek {
                return values().first { it.index == index }
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
