package net.accelf.itc_lms_unofficial.models

import net.accelf.itc_lms_unofficial.network.DocumentConverterFactory
import okhttp3.ResponseBody
import java.io.Serializable

data class Information(
    val text: String,
) : Serializable {

    class Converter(baseUrl: String) :
        DocumentConverterFactory.DocumentConverter<Information>(baseUrl) {

        override fun convert(value: ResponseBody): Information {
            document(value).let { document ->
                return Information(
                    document.select("#information .infotext ul").first()?.html() ?: "",
                )
            }
        }
    }
}
