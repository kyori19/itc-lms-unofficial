package net.accelf.itc_lms_unofficial.network

import com.google.gson.Gson
import net.accelf.itc_lms_unofficial.models.*
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class DocumentConverterFactory(private val gson: Gson) : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit,
    ): Converter<ResponseBody, *>? {
        val baseUri = retrofit.baseUrl().toString()
        return when (type) {
            AttendanceSend::class.java -> AttendanceSend.Converter(baseUri)
            CourseDetail::class.java -> CourseDetail.Converter(baseUri, gson)
            NotifyDetail::class.java -> NotifyDetail.Converter(baseUri)
            ReportDetail::class.java -> ReportDetail.Converter(baseUri)
            Settings::class.java -> Settings.Converter(baseUri)
            String::class.java -> StringConverter()
            TimeTable::class.java -> TimeTable.Converter(baseUri)
            Updates::class.java -> Updates.Converter(baseUri)
            else -> null
        }
    }

    abstract class DocumentConverter<T : Any>(private val baseUri: String) :
        Converter<ResponseBody, T?> {
        abstract override fun convert(value: ResponseBody): T?

        fun document(value: ResponseBody): Document {
            return Jsoup.parse(
                value.byteStream(),
                value.contentType()?.charset()?.name() ?: "UTF-8",
                baseUri,
                Parser.htmlParser()
            )
        }
    }

    class StringConverter : Converter<ResponseBody, String> {
        override fun convert(value: ResponseBody): String? {
            return value.string()
        }
    }
}
