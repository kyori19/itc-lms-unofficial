package net.accelf.itc_lms_unofficial.coursedetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.models.Attendance
import net.accelf.itc_lms_unofficial.ui.Icon
import net.accelf.itc_lms_unofficial.ui.NormalText
import net.accelf.itc_lms_unofficial.ui.Values
import net.accelf.itc_lms_unofficial.ui.Values.Theme.success
import net.accelf.itc_lms_unofficial.ui.Values.Theme.warning
import net.accelf.itc_lms_unofficial.ui.dateString

@Composable
@Preview
fun PreviewAttendance() {
    Column {
        Attendance(
            attendance = Attendance.sampleBefore,
            modifier = Modifier.padding(Values.Spacing.around),
        )

        Attendance(
            attendance = Attendance.samplePresent,
            modifier = Modifier.padding(Values.Spacing.around),
        )

        Attendance(
            attendance = Attendance.sampleLate,
            modifier = Modifier.padding(Values.Spacing.around),
        )

        Attendance(
            attendance = Attendance.sampleAbsent,
            modifier = Modifier.padding(Values.Spacing.around),
        )
    }
}

@Composable
fun Attendance(
    attendance: Attendance,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = when (attendance.status) {
                Attendance.AttendanceStatus.UNKNOWN -> Icons.Default.Cancel
                Attendance.AttendanceStatus.BEFORE -> Icons.Default.Schedule
                Attendance.AttendanceStatus.PRESENT -> Icons.Default.Check
                Attendance.AttendanceStatus.LATE -> Icons.Default.Schedule
                Attendance.AttendanceStatus.ABSENT -> Icons.Default.Remove
            },
            modifier = Modifier.padding(Values.Spacing.around),
            tint = when (attendance.status) {
                Attendance.AttendanceStatus.UNKNOWN,
                Attendance.AttendanceStatus.BEFORE,
                -> Color.Unspecified
                Attendance.AttendanceStatus.PRESENT -> MaterialTheme.colors.success
                Attendance.AttendanceStatus.LATE -> MaterialTheme.colors.warning
                Attendance.AttendanceStatus.ABSENT -> MaterialTheme.colors.error
            },
            contentDescription = stringResource(
                id = when (attendance.status) {
                    Attendance.AttendanceStatus.UNKNOWN -> R.string.hint_icon_unknown
                    Attendance.AttendanceStatus.BEFORE -> R.string.hint_icon_before
                    Attendance.AttendanceStatus.PRESENT -> R.string.hint_icon_present
                    Attendance.AttendanceStatus.LATE -> R.string.hint_icon_late
                    Attendance.AttendanceStatus.ABSENT -> R.string.hint_icon_absent
                },
            )
        )

        NormalText(
            text = dateString(attendance.date),
            modifier = Modifier.padding(Values.Spacing.around),
            style = MaterialTheme.typography.h6,
        )
    }
}
