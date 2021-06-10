package net.accelf.itc_lms_unofficial.coursedetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import net.accelf.itc_lms_unofficial.models.Forum
import net.accelf.itc_lms_unofficial.ui.NormalText
import net.accelf.itc_lms_unofficial.ui.TimeSpan
import net.accelf.itc_lms_unofficial.ui.Values

@Composable
@Preview
fun PreviewForum() {
    Forum(
        forum = Forum.sample,
    )
}

@Composable
fun Forum(
    forum: Forum,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NormalText(
            text = forum.title,
            modifier = Modifier.padding(Values.Spacing.around),
            style = MaterialTheme.typography.h6,
        )

        TimeSpan(
            start = forum.from,
            end = forum.until,
            modifier = Modifier.padding(Values.Spacing.around),
        )
    }
}
