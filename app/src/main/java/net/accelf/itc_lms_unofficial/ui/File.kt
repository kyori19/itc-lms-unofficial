package net.accelf.itc_lms_unofficial.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import net.accelf.itc_lms_unofficial.models.File
import net.accelf.itc_lms_unofficial.models.SubmittedFile
import net.accelf.itc_lms_unofficial.util.TIME_FORMAT

@Composable
@Preview
fun PreviewFile() {
    Column {
        File(SubmittedFile.sample)
        File(File.sample)
    }
}

@Composable
fun File(
    submittedFile: SubmittedFile,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        File(file = submittedFile.file)
        NormalText(
            text = TIME_FORMAT.format(submittedFile.submittedAt),
            modifier = Modifier.padding(Values.Spacing.around),
        )
    }
}

@Composable
fun File(
    file: File,
    modifier: Modifier = Modifier,
) {
    File(
        fileName = file.fileName,
        modifier = modifier,
    )
}

@Composable
fun File(
    fileName: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.InsertDriveFile,
            modifier = Modifier.padding(Values.Spacing.around),
        )
        NormalText(
            text = fileName,
            modifier = Modifier.padding(Values.Spacing.around),
        )
    }
}
