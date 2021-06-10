package net.accelf.itc_lms_unofficial.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import net.accelf.itc_lms_unofficial.models.QuillData
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color as ComposeColor

fun QuillData.annotatedString(): AnnotatedString {
    return buildAnnotatedString {
        content.forEach {
            val start = length
            append(it.text)
            val end = length
            it.attributes?.let { attrs ->
                attrs.color?.runCatching { AndroidColor.parseColor(this) }
                    ?.getOrNull()
                    ?.run { ComposeColor(this) }
                    ?.let { color ->
                        addStyle(SpanStyle(color = color), start, end)
                    }
                attrs.link?.let { link ->
                    addStringAnnotation("URL", link, start, end)
                }
            }
        }
    }
}
