package net.accelf.itc_lms_unofficial.models

import net.accelf.itc_lms_unofficial.network.DocumentConverterFactory
import net.accelf.itc_lms_unofficial.util.second
import net.accelf.itc_lms_unofficial.util.toTimeSpan
import okhttp3.ResponseBody
import java.io.Serializable
import java.util.*

data class NotifyDetail(
    val title: String,
    val senderName: String,
    val from: Date?,
    val to: Date?,
    val text: String
) : Serializable {

    class NotifyDetailConverter(baseUrl: String) :
        DocumentConverterFactory.DocumentConverter<NotifyDetail>(baseUrl) {
        override fun convert(value: ResponseBody): NotifyDetail? {
            document(value).let { document ->
                val blocks =
                    document.select("#information_view .subblock_form span:not(#osiraseTitle)")
                val (from, to) = blocks.first().text().toTimeSpan()

                return NotifyDetail(
                    document.select("#osiraseTitle").first().text(),
                    blocks.second().text(),
                    from,
                    to,
                    document.select(".textareaContents").first().html()
                )
            }
        }
    }
}
