package net.accelf.itc_lms_unofficial.util

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.URLSpan
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class QuillAttributes(
    val color: String?,
    val link: String?,
)

data class QuillElement(
    @SerializedName("insert") val text: String,
    val attributes: QuillAttributes?,
)

data class QuillData(
    @SerializedName("ops") val content: List<QuillElement>,
) {

    fun toSpanned(): Spanned {
        val builder = SpannableStringBuilder()
        content.forEach {
            val start = builder.length
            builder.append(it.text)
            val end = builder.length
            it.attributes?.let { attrs ->
                attrs.color?.let { color ->
                    builder.setSpan(ForegroundColorSpan(Color.parseColor(color)),
                        start,
                        end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                attrs.link?.let { link ->
                    builder.setSpan(URLSpan(link), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }
        return builder
    }
}

fun String.parseQuill(gson: Gson): QuillData? {
    return gson.fromJson(this, QuillData::class.java)
}
