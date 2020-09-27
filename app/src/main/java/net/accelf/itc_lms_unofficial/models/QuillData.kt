package net.accelf.itc_lms_unofficial.models

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.URLSpan
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class QuillData(
    @SerializedName("ops") val content: List<QuillElement>,
) : Serializable {

    fun toSpanned(): Spanned {
        val builder = SpannableStringBuilder()
        content.forEach {
            val start = builder.length
            builder.append(it.text)
            val end = builder.length
            it.attributes?.let { attrs ->
                attrs.color?.let { color ->
                    kotlin.runCatching { Color.parseColor(color) }
                        .onSuccess { parsedColor ->
                            builder.setSpan(ForegroundColorSpan(parsedColor),
                                start,
                                end,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                }
                attrs.link?.let { link ->
                    builder.setSpan(URLSpan(link), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }
        return builder
    }

    data class QuillAttributes(
        val color: String?,
        val link: String?,
    ) : Serializable

    data class QuillElement(
        @SerializedName("insert") val text: String,
        val attributes: QuillAttributes?,
    ) : Serializable

    companion object {
        fun String.parseQuill(gson: Gson): QuillData? {
            return gson.fromJson(this, QuillData::class.java)
        }
    }
}
