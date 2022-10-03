package net.accelf.itc_lms_unofficial.coursedetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.models.AttendanceSend
import net.accelf.itc_lms_unofficial.ui.ErrorText
import net.accelf.itc_lms_unofficial.ui.PasswordField
import net.accelf.itc_lms_unofficial.ui.Values

@ExperimentalComposeUiApi
@Composable
@Preview
private fun PreviewSendAttendanceDialog() {
    SendAttendanceDialog(
        attendanceSend = AttendanceSend.sample,
        onSubmit = { _, _ -> },
        onCancel = {},
    )
}

@ExperimentalComposeUiApi
@Composable
fun SendAttendanceDialog(
    attendanceSend: AttendanceSend,
    onSubmit: (String, String) -> Unit,
    onCancel: () -> Unit = {},
) {
    var password by remember { mutableStateOf(attendanceSend.password) }
    var comment by remember { mutableStateOf(attendanceSend.comment) }
    var enabled by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onCancel,
        confirmButton = {
            TextButton(
                onClick = {
                    onSubmit(password, comment)
                },
                enabled = enabled,
            ) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancel,
            ) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        },
        title = { Text(text = stringResource(id = R.string.dialog_title_send_attendance)) },
        text = {
            SendAttendanceDialogContent(
                attendanceSend = attendanceSend,
                password = password,
                comment = comment,
                onPasswordChange = {
                    password = it
                    enabled = it.isNotEmpty()
                },
                onCommentChange = { comment = it },
            )
        }
    )
}

@ExperimentalComposeUiApi
@Composable
@Preview
private fun PreviewSendAttendanceDialogContent() {
    val attendanceSend = AttendanceSend.sample
    var password by remember { mutableStateOf(attendanceSend.password) }
    var comment by remember { mutableStateOf(attendanceSend.comment) }
    SendAttendanceDialogContent(
        attendanceSend = attendanceSend,
        password = password,
        comment = comment,
        onPasswordChange = { password = it },
        onCommentChange = { comment = it },
        modifier = Modifier.padding(Values.Spacing.around),
    )
}

@ExperimentalComposeUiApi
@Composable
private fun SendAttendanceDialogContent(
    attendanceSend: AttendanceSend,
    password: String,
    comment: String,
    onPasswordChange: (String) -> Unit,
    onCommentChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        if (attendanceSend.sent) {
            ErrorText(
                text = stringResource(id = R.string.text_attendance_already_sent),
                modifier = Modifier.padding(Values.Spacing.around),
            )
        }

        var isErrorOnPassword by remember { mutableStateOf(attendanceSend.errorOnPassword.isNotEmpty()) }
        var isErrorOnComment by remember { mutableStateOf(attendanceSend.errorOnComment.isNotEmpty()) }
        if (isErrorOnPassword || isErrorOnComment) {
            ErrorText(
                text = stringResource(id = R.string.text_attendance_error),
                modifier = Modifier.padding(Values.Spacing.around),
            )
        }

        PasswordField(
            value = password,
            modifier = Modifier.padding(Values.Spacing.around),
            setValue = {
                onPasswordChange(it)
                isErrorOnPassword = false
            },
            label = { Text(text = stringResource(id = R.string.input_hint_attendance_password)) },
            isError = isErrorOnPassword,
        )
        if (isErrorOnPassword) {
            ErrorText(
                text = attendanceSend.errorOnPassword,
                modifier = Modifier.padding(
                    start = Values.Spacing.around,
                    end = Values.Spacing.around,
                    bottom = Values.Spacing.around,
                ),
            )
        }

        TextField(
            value = comment,
            onValueChange = {
                onCommentChange(it)
                isErrorOnComment = false
            },
            label = { Text(text = stringResource(id = R.string.input_hint_attendance_comment)) },
            modifier = Modifier.padding(Values.Spacing.around),
        )
        if (isErrorOnComment) {
            ErrorText(
                text = attendanceSend.errorOnComment,
                modifier = Modifier.padding(
                    start = Values.Spacing.around,
                    end = Values.Spacing.around,
                    bottom = Values.Spacing.around,
                ),
            )
        }
    }
}
