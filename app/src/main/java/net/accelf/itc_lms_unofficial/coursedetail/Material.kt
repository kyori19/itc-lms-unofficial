package net.accelf.itc_lms_unofficial.coursedetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Movie
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.models.Material
import net.accelf.itc_lms_unofficial.ui.Icon
import net.accelf.itc_lms_unofficial.ui.NormalText
import net.accelf.itc_lms_unofficial.ui.TimeSpan
import net.accelf.itc_lms_unofficial.ui.Values

@Composable
@Preview
fun PreviewMaterial() {
    Column {
        Material(
            material = Material.sampleFile,
            modifier = Modifier.padding(Values.Spacing.around),
        )
        Material(
            material = Material.sampleLink,
            modifier = Modifier.padding(Values.Spacing.around),
        )
    }
}

@Composable
fun Material(
    material: Material,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = when (material.type) {
                Material.MaterialType.FILE -> Icons.Default.InsertDriveFile
                Material.MaterialType.LINK -> Icons.Default.Link
                Material.MaterialType.VIDEO -> Icons.Default.Movie
            },
            modifier = Modifier.padding(Values.Spacing.around),
            contentDescription = stringResource(
                id = when (material.type) {
                    Material.MaterialType.FILE -> R.string.hint_icon_file
                    Material.MaterialType.LINK -> R.string.hint_icon_link
                    Material.MaterialType.VIDEO -> R.string.hint_icon_video
                },
            )
        )

        NormalText(
            text = material.name,
            modifier = Modifier
                .padding(Values.Spacing.around)
                .weight(1f),
            style = MaterialTheme.typography.h6,
        )

        TimeSpan(
            start = material.createdAt,
            end = material.until,
            modifier = Modifier.padding(Values.Spacing.around),
        )
    }
}
