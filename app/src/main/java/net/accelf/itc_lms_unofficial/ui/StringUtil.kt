package net.accelf.itc_lms_unofficial.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.util.TIME_FORMAT
import java.util.*

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
