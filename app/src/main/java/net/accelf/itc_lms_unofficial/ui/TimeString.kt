package net.accelf.itc_lms_unofficial.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.util.DATE_FORMAT
import net.accelf.itc_lms_unofficial.util.TIME_FORMAT
import java.util.*

@Composable
fun dateString(date: Date?): String {
    return date?.let { DATE_FORMAT.format(date) } ?: ""
}

@Composable
fun Time(
    date: Date?,
    modifier: Modifier = Modifier,
) {
    NormalText(
        text = timeString(date),
        modifier = modifier,
    )
}

@Composable
fun timeString(date: Date?): String {
    return date?.let { TIME_FORMAT.format(it) } ?: ""
}

@Composable
fun TimeSpan(
    start: Date?,
    end: Date?,
    modifier: Modifier = Modifier,
    multiline: Boolean = true,
) {
    NormalText(
        text = timeSpanString(start, end, multiline),
        modifier = modifier,
    )
}

@Composable
fun timeSpanString(start: Date?, end: Date?, multiline: Boolean = true): String {
    return stringResource(
        id = when (multiline) {
            true -> R.string.text_time_span_multi_lines
            false -> R.string.text_time_span_single_line
        },
        start?.let { TIME_FORMAT.format(it) } ?: "",
        end?.let { TIME_FORMAT.format(it) } ?: "",
    )
}
