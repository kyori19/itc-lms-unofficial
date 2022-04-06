package net.accelf.itc_lms_unofficial.coursedetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.models.Test
import net.accelf.itc_lms_unofficial.ui.Icon
import net.accelf.itc_lms_unofficial.ui.NormalText
import net.accelf.itc_lms_unofficial.ui.TimeSpan
import net.accelf.itc_lms_unofficial.ui.Values
import net.accelf.itc_lms_unofficial.ui.Values.Theme.success

@Composable
@Preview
fun PreviewTest() {
    Test(
        test = Test.sample,
        courseId = "",
        modifier = Modifier.padding(Values.Spacing.around),
    )
}

@Composable
fun Test(
    test: Test,
    courseId: String,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    Row(
        modifier = modifier
            .clickable(test.status == Test.TestStatus.NOT_TAKEN) {
                uriHandler.openUri(test.getTakeUrl(courseId))
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = when (test.status) {
                Test.TestStatus.TAKEN -> Icons.Default.Check
                Test.TestStatus.NOT_TAKEN -> Icons.Default.Cancel
                Test.TestStatus.UNKNOWN -> Icons.Default.Remove
            },
            modifier = Modifier.padding(Values.Spacing.around),
            tint = when (test.status) {
                Test.TestStatus.TAKEN -> MaterialTheme.colors.success
                Test.TestStatus.NOT_TAKEN -> MaterialTheme.colors.error
                Test.TestStatus.UNKNOWN -> Color.Unspecified
            },
            contentDescription = stringResource(
                id = when (test.status) {
                    Test.TestStatus.NOT_TAKEN -> R.string.hint_icon_not_taken
                    Test.TestStatus.TAKEN -> R.string.hint_icon_taken
                    Test.TestStatus.UNKNOWN -> R.string.hint_icon_unknown
                },
            )
        )

        NormalText(
            text = test.title,
            modifier = Modifier
                .padding(Values.Spacing.around)
                .weight(1f),
            style = MaterialTheme.typography.h6,
        )

        TimeSpan(
            start = test.from,
            end = test.until,
            modifier = Modifier.padding(Values.Spacing.around),
        )
    }
}
