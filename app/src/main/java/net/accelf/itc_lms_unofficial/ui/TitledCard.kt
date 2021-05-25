package net.accelf.itc_lms_unofficial.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun PreviewTitledCard() {
    TitledCard(
        title = "Card Title",
        modifier = Modifier
            .padding(Values.Spacing.around)
            .fillMaxWidth(),
    ) {
        Button(
            onClick = {},
            modifier = Modifier
                .padding(Values.Spacing.around)
                .fillMaxWidth(),
        ) {
            Text(
                text = "Card content"
            )
        }
    }
}

@Composable
fun TitledCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
    ) {
        Column {
            NormalText(
                text = title,
                modifier = Modifier.padding(Values.Spacing.around),
                style = MaterialTheme.typography.h5,
            )
            content()
        }
    }
}
