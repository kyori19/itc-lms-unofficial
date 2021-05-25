package net.accelf.itc_lms_unofficial.reportdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.file.download.Downloadable
import net.accelf.itc_lms_unofficial.file.download.Downloadable.Companion.preparePermissionRequestForDownloadable
import net.accelf.itc_lms_unofficial.models.File
import net.accelf.itc_lms_unofficial.models.Report
import net.accelf.itc_lms_unofficial.models.ReportDetail
import net.accelf.itc_lms_unofficial.permission.PermissionRequestable
import net.accelf.itc_lms_unofficial.ui.*
import net.accelf.itc_lms_unofficial.ui.Values.Theme.success
import net.accelf.itc_lms_unofficial.util.TIME_FORMAT
import net.accelf.itc_lms_unofficial.util.fromHtml
import javax.inject.Inject

@AndroidEntryPoint
class ReportDetailFragment : Fragment(),
    PermissionRequestable, Downloadable.ProvidesGson {

    private lateinit var reportDetail: ReportDetail

    @Inject
    override lateinit var gson: Gson

    private var downloadable: Downloadable? = null
    override var permissionRequestLauncher: ActivityResultLauncher<String> =
        preparePermissionRequestForDownloadable {
            val d = downloadable!!
            downloadable = null
            d
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            reportDetail = it.getSerializable(ARG_REPORT_DETAIL) as ReportDetail
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return compose {
            ReportDetailFragmentContent(reportDetail)
        }
    }

    @Composable
    @Preview
    private fun PreviewReportDetailFragmentContent() {
        ReportDetailFragmentContent(ReportDetail.sample)
    }

    @Composable
    private fun ReportDetailFragmentContent(reportDetail: ReportDetail) {
        Column(
            modifier = Modifier.padding(Values.Spacing.around),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NormalText(
                    text = reportDetail.title,
                    modifier = Modifier
                        .padding(Values.Spacing.around)
                        .weight(weight = 1f),
                    style = MaterialTheme.typography.h5,
                    color = MaterialTheme.colors.secondary,
                )
                NormalText(
                    text = timeSpanString(reportDetail.from, reportDetail.until),
                    modifier = Modifier.padding(Values.Spacing.around),
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = when (reportDetail.status) {
                        Report.ReportStatus.NOT_SUBMITTED -> Icons.Default.Clear
                        Report.ReportStatus.SUBMITTED_IN_TIME -> Icons.Default.Check
                        Report.ReportStatus.SUBMITTED_AFTER_DEADLINE -> Icons.Default.Schedule
                        Report.ReportStatus.TEMPORARILY_SAVED -> Icons.Default.Save
                    },
                    modifier = Modifier.padding(Values.Spacing.around),
                    contentDescription = stringResource(
                        id = when (reportDetail.status) {
                            Report.ReportStatus.NOT_SUBMITTED -> R.string.hint_icon_not_submitted
                            Report.ReportStatus.SUBMITTED_IN_TIME -> R.string.hint_icon_submitted_in_time
                            Report.ReportStatus.SUBMITTED_AFTER_DEADLINE -> R.string.hint_icon_submitted_after_deadline
                            Report.ReportStatus.TEMPORARILY_SAVED -> R.string.hint_icon_temporarily_saved
                        },
                    ),
                    tint = when (reportDetail.status == Report.ReportStatus.SUBMITTED_IN_TIME) {
                        true -> MaterialTheme.colors.success
                        false -> MaterialTheme.colors.error
                    },
                )
                NormalText(
                    text = stringResource(
                        id = when (reportDetail.status) {
                            Report.ReportStatus.NOT_SUBMITTED -> R.string.hint_icon_not_submitted
                            Report.ReportStatus.SUBMITTED_IN_TIME -> R.string.hint_icon_submitted_in_time
                            Report.ReportStatus.SUBMITTED_AFTER_DEADLINE -> R.string.hint_icon_submitted_after_deadline
                            Report.ReportStatus.TEMPORARILY_SAVED -> R.string.hint_icon_temporarily_saved
                        },
                    ),
                    modifier = Modifier.padding(Values.Spacing.around),
                )

                Icon(
                    imageVector = when (reportDetail.afterDeadlineSubmittable) {
                        true -> Icons.Default.Check
                        false -> Icons.Default.Clear
                    },
                    contentDescription = stringResource(
                        id = when (reportDetail.afterDeadlineSubmittable) {
                            true -> R.string.hint_icon_late_submittable
                            false -> R.string.hint_icon_not_late_submittable
                        }
                    ),
                    tint = when (reportDetail.afterDeadlineSubmittable) {
                        true -> MaterialTheme.colors.success
                        false -> MaterialTheme.colors.error
                    },
                )
                NormalText(
                    text = stringResource(
                        id = when (reportDetail.afterDeadlineSubmittable) {
                            true -> R.string.hint_icon_late_submittable
                            false -> R.string.hint_icon_not_late_submittable
                        },
                    ),
                    modifier = Modifier.padding(Values.Spacing.around),
                )
            }

            reportDetail.submittedAt?.let {
                NormalText(
                    text = stringResource(
                        id = R.string.text_report_submitted_at,
                        TIME_FORMAT.format(it),
                    ),
                    modifier = Modifier.padding(Values.Spacing.around),
                )
            }

            if (reportDetail.description.isNotEmpty()) {
                SpannedText(
                    text = reportDetail.description.fromHtml(),
                    modifier = Modifier.padding(Values.Spacing.around),
                )
            }

            LazyColumn {
                items(reportDetail.attachmentFiles) {
                    Row {
                        File(
                            file = it,
                            modifier = Modifier
                                .clickable { openFile(it) }
                                .weight(1f),
                        )
                    }
                }
            }

            if (
                reportDetail.submissionType == ReportDetail.SubmissionType.FILE
                || reportDetail.submittedFiles.isNotEmpty()
            ) {
                TitledCard(
                    title = stringResource(id = R.string.title_report_submit_files),
                    modifier = Modifier.padding(Values.Spacing.around),
                ) {
                    LazyColumn {
                        items(reportDetail.submittedFiles) {
                            Row {
                                File(
                                    submittedFile = it,
                                    modifier = Modifier
                                        .padding(Values.Spacing.around)
                                        .clickable { openFile(it.file) }
                                        .weight(1f),
                                )
                            }
                        }
                    }
                }
            }

            if (
                reportDetail.submissionType == ReportDetail.SubmissionType.TEXT_INPUT
                || reportDetail.submittedText.isNotEmpty()
            ) {
                TitledCard(
                    title = stringResource(id = R.string.title_report_submit_text),
                    modifier = Modifier.padding(Values.Spacing.around),
                ) {
                    NormalText(
                        text = reportDetail.submittedText,
                        modifier = Modifier.padding(Values.Spacing.around),
                    )
                }
            }

            reportDetail.fedBackAt?.let { fedBackAt ->
                TitledCard(
                    title = stringResource(id = R.string.title_report_feedback),
                    modifier = Modifier.padding(Values.Spacing.around),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        NormalText(
                            text = stringResource(id = R.string.title_report_fed_back_by),
                            modifier = Modifier
                                .padding(Values.Spacing.around)
                                .weight(1f),
                        )
                        NormalText(
                            text = reportDetail.fedBackBy,
                            modifier = Modifier.padding(Values.Spacing.around),
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        NormalText(
                            text = stringResource(id = R.string.title_report_feedback_score),
                            modifier = Modifier
                                .padding(Values.Spacing.around)
                                .weight(1f),
                        )
                        NormalText(
                            text = reportDetail.feedbackScore,
                            modifier = Modifier.padding(Values.Spacing.around),
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        NormalText(
                            text = stringResource(id = R.string.title_report_feedback_comment),
                            modifier = Modifier
                                .padding(Values.Spacing.around)
                                .weight(1f),
                        )
                        NormalText(
                            text = reportDetail.feedbackComment,
                            modifier = Modifier.padding(Values.Spacing.around),
                        )
                    }

                    reportDetail.feedbackFile?.let {
                        File(
                            file = it,
                            modifier = Modifier.clickable { openFile(it) },
                        )
                    }

                    Row {
                        NormalText(
                            text = TIME_FORMAT.format(fedBackAt),
                            modifier = Modifier
                                .padding(Values.Spacing.around)
                                .weight(1f),
                            textAlign = TextAlign.End,
                        )
                    }
                }
            }
        }
    }

    private fun openFile(file: File) {
        downloadable = Downloadable.reportFile(reportDetail.courseId, file)
        downloadable!!.open(this)
    }

    companion object {
        private const val ARG_REPORT_DETAIL = "report_detail"

        @JvmStatic
        fun newInstance(reportDetail: ReportDetail): ReportDetailFragment {
            return ReportDetailFragment()
                .apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_REPORT_DETAIL, reportDetail)
                    }
                }
        }
    }
}
