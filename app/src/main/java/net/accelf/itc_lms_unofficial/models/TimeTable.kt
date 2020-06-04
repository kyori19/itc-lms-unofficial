package net.accelf.itc_lms_unofficial.models

import net.accelf.itc_lms_unofficial.network.DocumentConverterFactory
import okhttp3.ResponseBody

data class TimeTable(
    val name: String
) {
    class TimeTableConverter(baseUrl: String) :
        DocumentConverterFactory.DocumentConverter<TimeTable>(baseUrl) {
        override fun convert(value: ResponseBody): TimeTable? {
            document(value).let {
                return TimeTable(
                    it.select("#page_contents .login_view_name").first().text()
                )
            }
        }
    }
}
