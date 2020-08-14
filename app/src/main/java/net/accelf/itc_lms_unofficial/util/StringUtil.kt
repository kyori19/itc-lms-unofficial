package net.accelf.itc_lms_unofficial.util

import android.content.Context
import android.text.Spanned
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import net.accelf.itc_lms_unofficial.R
import java.text.SimpleDateFormat
import java.util.*

val TIME_SPAN_REGEX = Regex("""\d{4}/\d{2}/\d{2}\s\d{2}:\d{2}""")
val TIME_FORMAT = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US)
val DATE_FORMAT = SimpleDateFormat("yyyy/MM/dd", Locale.US)
val TIME_SECONDS_FORMAT = SimpleDateFormat("yyyy-MM-dd hh:MM:ss.S", Locale.US)

fun String.toTimeSpan(): List<Date> {
    return TIME_SPAN_REGEX.findAll(this).map {
        TIME_FORMAT.parse(it.value)
    }.toList()
}

fun String.toDateTime(): Date? {
    return TIME_FORMAT.parse(this)
}

fun String.toDate(): Date? {
    return DATE_FORMAT.parse(this)
}

fun String.toTimeSeconds(): Date? {
    return TIME_SECONDS_FORMAT.parse(this)
}

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
