package net.accelf.itc_lms_unofficial.coursedetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.models.Message
import net.accelf.itc_lms_unofficial.ui.*
import net.accelf.itc_lms_unofficial.ui.Values.Theme.success
import net.accelf.itc_lms_unofficial.ui.Values.Theme.warning

@Composable
@Preview
fun PreviewMessage() {
    Message(
        message = Message.sample,
        modifier = Modifier.padding(Values.Spacing.around),
    )
}

@Composable
fun Message(
    message: Message,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = when (message.status) {
                    Message.MessageStatus.WAITING_FOR_ANSWER -> Icons.Default.NotificationsNone
                    Message.MessageStatus.HAS_ANSWER -> Icons.Default.Notifications
                    Message.MessageStatus.COMPLETED -> Icons.Default.Check
                },
                modifier = Modifier.padding(Values.Spacing.around),
                tint = when (message.status) {
                    Message.MessageStatus.WAITING_FOR_ANSWER -> Color.Unspecified
                    Message.MessageStatus.HAS_ANSWER -> MaterialTheme.colors.warning
                    Message.MessageStatus.COMPLETED -> MaterialTheme.colors.success
                },
                contentDescription = stringResource(
                    id = when (message.status) {
                        Message.MessageStatus.WAITING_FOR_ANSWER -> R.string.hint_icon_waiting_for_answer
                        Message.MessageStatus.HAS_ANSWER -> R.string.hint_icon_has_answer
                        Message.MessageStatus.COMPLETED -> R.string.hint_icon_completed
                    },
                )
            )

            NormalText(
                text = message.title,
                modifier = Modifier
                    .padding(Values.Spacing.around)
                    .weight(1f),
                style = MaterialTheme.typography.h6,
            )

            Time(
                date = message.createdAt,
                modifier = Modifier.padding(Values.Spacing.around),
            )
        }

        NormalText(
            text = when (message.status) {
                Message.MessageStatus.WAITING_FOR_ANSWER -> stringResource(id = R.string.text_message_sent)
                Message.MessageStatus.HAS_ANSWER -> stringResource(
                    id = R.string.text_message_got_answer_at,
                    timeString(message.actedAt),
                )
                Message.MessageStatus.COMPLETED -> stringResource(
                    id = R.string.text_message_completed_by,
                    message.actorName ?: "",
                )
            },
            modifier = Modifier
                .padding(Values.Spacing.around)
                .fillMaxWidth(1f),
            textAlign = TextAlign.End,
        )
    }
}
