package net.accelf.itc_lms_unofficial.coursedetail

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.models.Report
import net.accelf.itc_lms_unofficial.ui.Icon
import net.accelf.itc_lms_unofficial.ui.NormalText
import net.accelf.itc_lms_unofficial.ui.TimeSpan
import net.accelf.itc_lms_unofficial.ui.Values
import net.accelf.itc_lms_unofficial.ui.Values.Theme.success

@Composable
@Preview
fun PreviewReport() {
    Report(
        report = Report.sample,
        modifier = Modifier.padding(Values.Spacing.around),
    )
}

@Composable
fun Report(
    report: Report,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = when (report.status) {
                Report.ReportStatus.NOT_SUBMITTED -> Icons.Default.Cancel
                Report.ReportStatus.SUBMITTED_IN_TIME -> Icons.Default.Check
                Report.ReportStatus.SUBMITTED_AFTER_DEADLINE -> Icons.Default.Schedule
                Report.ReportStatus.TEMPORARILY_SAVED -> Icons.Default.Save
            },
            modifier = Modifier.padding(Values.Spacing.around),
            tint = when (report.status) {
                Report.ReportStatus.SUBMITTED_IN_TIME -> MaterialTheme.colors.success
                Report.ReportStatus.NOT_SUBMITTED,
                Report.ReportStatus.SUBMITTED_AFTER_DEADLINE,
                Report.ReportStatus.TEMPORARILY_SAVED,
                -> MaterialTheme.colors.error
            },
            contentDescription = stringResource(
                id = when (report.status) {
                    Report.ReportStatus.NOT_SUBMITTED -> R.string.hint_icon_not_submitted
                    Report.ReportStatus.SUBMITTED_IN_TIME -> R.string.hint_icon_submitted_in_time
                    Report.ReportStatus.SUBMITTED_AFTER_DEADLINE -> R.string.hint_icon_submitted_after_deadline
                    Report.ReportStatus.TEMPORARILY_SAVED -> R.string.hint_icon_temporarily_saved
                },
            )
        )

        NormalText(
            text = report.title,
            modifier = Modifier
                .padding(Values.Spacing.around)
                .weight(1f),
            style = MaterialTheme.typography.h6,
        )

        TimeSpan(
            start = report.from,
            end = report.until,
            modifier = Modifier.padding(Values.Spacing.around),
        )
    }
}
