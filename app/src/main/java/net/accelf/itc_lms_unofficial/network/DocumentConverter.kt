package net.accelf.itc_lms_unofficial.network

import net.accelf.itc_lms_unofficial.models.CourseDetail
import net.accelf.itc_lms_unofficial.models.TimeTable
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class DocumentConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        val baseUri = retrofit.baseUrl().toString()
        return when (type) {
            CourseDetail::class.java -> CourseDetail.CourseDetailConverter(baseUri)
            TimeTable::class.java -> TimeTable.TimeTableConverter(baseUri)
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
}
