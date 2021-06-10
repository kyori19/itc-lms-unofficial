package net.accelf.itc_lms_unofficial.coursedetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.di.CustomLinkMovementMethod
import net.accelf.itc_lms_unofficial.models.CourseContent
import net.accelf.itc_lms_unofficial.ui.*
import net.accelf.itc_lms_unofficial.util.fromHtml

@Composable
@Preview
fun PreviewCourseContent() {
    CourseContent(
        courseContent = CourseContent.sample,
        linkMovementMethod = CustomLinkMovementMethod(),
        focusCourseContentResourceId = net.accelf.itc_lms_unofficial.models.Material.sampleFile.materialId
    )
}

@Composable
fun CourseContent(
    courseContent: CourseContent,
    modifier: Modifier = Modifier,
    linkMovementMethod: CustomLinkMovementMethod,
    onMaterialClick: (net.accelf.itc_lms_unofficial.models.Material) -> Unit = {},
    focusCourseContentResourceId: String? = null,
) {
    TitledCard(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NormalText(
                    text = courseContent.title,
                    modifier = Modifier
                        .padding(Values.Spacing.around)
                        .weight(1f),
                    style = MaterialTheme.typography.h5,
                )

                TimeSpan(
                    start = courseContent.from,
                    end = courseContent.until,
                    modifier = Modifier.padding(Values.Spacing.around),
                )
            }
        },
        modifier = modifier,
    ) {
        SpannedText(
            text = courseContent.summary.fromHtml(),
            modifier = Modifier.padding(Values.Spacing.around),
        ) {
            movementMethod = linkMovementMethod
        }

        ExpandableCard(
            title = stringResource(R.string.title_materials),
            defaultExpanded = courseContent.materials
                .indexOfFirst { it.materialId == focusCourseContentResourceId } != -1,
        ) {
            Column {
                courseContent.materials.forEach { material ->
                    // TODO: appeal specific material
                    Material(
                        material = material,
                        modifier = Modifier
                            .padding(Values.Spacing.around)
                            .clickable {
                                onMaterialClick(material)
                            },
                    )
                }
            }
        }
    }
}
