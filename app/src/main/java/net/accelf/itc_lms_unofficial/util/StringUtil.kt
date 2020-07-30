package net.accelf.itc_lms_unofficial.util

import android.content.Context
import android.text.Spanned
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.models.TIME_FORMAT
import java.util.*

fun CharSequence?.isNotNullOrEmpty(): Boolean {
    return !isNullOrEmpty()
}

fun Context.timeSpanToString(start: Date?, end: Date?): String {
    return getString(R.string.text_time_span_multi_lines,
        start?.let { TIME_FORMAT.format(it) },
        end?.let { TIME_FORMAT.format(it) })
}

fun String.fromHtml(): Spanned {
    return HtmlCompat.fromHtml(this, FROM_HTML_MODE_COMPACT)
}
