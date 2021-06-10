package net.accelf.itc_lms_unofficial.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
private fun PreviewErrorText() {
    ErrorText(
        text = "Sample error text",
        modifier = Modifier.padding(Values.Spacing.around),
    )
}

@Composable
fun ErrorText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier,
        color = MaterialTheme.colors.error,
        style = MaterialTheme.typography.caption,
    )
}
