package net.accelf.itc_lms_unofficial.util

import android.annotation.SuppressLint
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.URLSpan
import androidx.core.text.getSpans
import androidx.core.text.set
import androidx.core.util.PatternsCompat

@SuppressLint("RestrictedApi")
fun Spanned.autoLink(): Spanned {
    val builder = SpannableStringBuilder(this)
    val matcher = PatternsCompat.AUTOLINK_WEB_URL.matcher(this)
    while (matcher.find()) {
        val start = matcher.start()
        val end = matcher.end()

        val urlSpans = builder.getSpans<URLSpan>(start, end)
        if (urlSpans.isEmpty()) {
            builder[start, end] = AutoURLSpan(substring(start, end))
        }
    }
    return builder
}

class AutoURLSpan(url: String) : URLSpan(url)
