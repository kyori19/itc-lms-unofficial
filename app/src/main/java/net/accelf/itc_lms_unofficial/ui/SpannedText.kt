package net.accelf.itc_lms_unofficial.ui

import android.text.Spanned
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun SpannedText(
    text: Spanned,
    modifier: Modifier = Modifier,
    update: (TextView).() -> Unit = ({}),
) {
    AndroidView(
        { TextView(it) },
        modifier = modifier,
    ) {
        it.text = text
        update(it)
    }
}
