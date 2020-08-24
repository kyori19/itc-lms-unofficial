package net.accelf.itc_lms_unofficial.reportdetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_report_detail.*
import net.accelf.itc_lms_unofficial.PdfActivity
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.models.Report
import net.accelf.itc_lms_unofficial.models.ReportDetail
import net.accelf.itc_lms_unofficial.util.TIME_FORMAT
import net.accelf.itc_lms_unofficial.util.set
import net.accelf.itc_lms_unofficial.util.showViewsAndDoWhen
import net.accelf.itc_lms_unofficial.util.timeSpanToString

class ReportDetailFragment : Fragment(R.layout.fragment_report_detail) {

    private lateinit var reportDetail: ReportDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            reportDetail = it.getSerializable(ARG_REPORT_DETAIL) as ReportDetail
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleReport.text = reportDetail.title

        textReportDate.apply {
            text = context.timeSpanToString(reportDetail.from, reportDetail.until)
        }

        iconReportStatus.apply {
            setImageResource(
                when (reportDetail.status) {
                    Report.ReportStatus.NOT_SUBMITTED -> R.drawable.ic_cancel
                    Report.ReportStatus.SUBMITTED_IN_TIME -> R.drawable.ic_check
                    Report.ReportStatus.SUBMITTED_AFTER_DEADLINE -> R.drawable.ic_time
                }
            )
            contentDescription = context.getString(
                when (reportDetail.status) {
                    Report.ReportStatus.NOT_SUBMITTED -> R.string.hint_icon_not_submitted
                    Report.ReportStatus.SUBMITTED_IN_TIME -> R.string.hint_icon_submitted_in_time
                    Report.ReportStatus.SUBMITTED_AFTER_DEADLINE -> R.string.hint_icon_submitted_after_deadline
                }
            )
        }

        textReportStatus.text = getString(
            when (reportDetail.status) {
                Report.ReportStatus.NOT_SUBMITTED -> R.string.hint_icon_not_submitted
                Report.ReportStatus.SUBMITTED_IN_TIME -> R.string.hint_icon_submitted_in_time
                Report.ReportStatus.SUBMITTED_AFTER_DEADLINE -> R.string.hint_icon_submitted_after_deadline
            }
        )

        iconReportLateSubmittable.apply {
            setImageResource(
                when (reportDetail.afterDeadlineSubmittable) {
                    true -> R.drawable.ic_check
                    false -> R.drawable.ic_cancel
                }
            )
            contentDescription = context.getString(
                when (reportDetail.afterDeadlineSubmittable) {
                    true -> R.string.hint_icon_late_submittable
                    false -> R.string.hint_icon_not_late_submittable
                }
            )
        }

        textReportLateSubmittable.text = getString(
            when (reportDetail.afterDeadlineSubmittable) {
                true -> R.string.hint_icon_late_submittable
                false -> R.string.hint_icon_not_late_submittable
            }
        )

        showViewsAndDoWhen(reportDetail.submittedAt != null, textReportSubmittedDate) {
            textReportSubmittedDate.text = getString(R.string.text_report_submitted_at,
                TIME_FORMAT.format(reportDetail.submittedAt!!))
        }

        showViewsAndDoWhen(reportDetail.description.isNotEmpty(), textReportDescription) {
            textReportDescription.text = reportDetail.description
        }

        showViewsAndDoWhen(reportDetail.attachmentFile != null,
            iconAttachmentFile,
            textAttachmentFileName) {
            listOf(iconAttachmentFile, textAttachmentFileName).forEach {
                it.setOnClickListener { view ->
                    if (reportDetail.attachmentFile!!.fileName.endsWith(".pdf")) {
                        startActivity(PdfActivity.intent(view.context,
                            reportDetail.attachmentFile!!.objectName))
                    }
                }
            }

            textAttachmentFileName.text = reportDetail.attachmentFile!!.fileName
        }

        showViewsAndDoWhen(reportDetail.submissionType == ReportDetail.SubmissionType.FILE
                || reportDetail.submittedFiles.isNotEmpty(), cardReportFiles) {

            @Suppress("UNCHECKED_CAST")
            listReportFiles.set(reportDetail.submittedFiles,
                SubmittedFileAdapter::class.java as Class<RecyclerView.Adapter<RecyclerView.ViewHolder>>)
        }

        showViewsAndDoWhen(
            reportDetail.submissionType == ReportDetail.SubmissionType.TEXT_INPUT
                    || reportDetail.submittedText.isNotEmpty(),
            cardReportText
        ) {
            textReportText.text = reportDetail.submittedText
        }

        showViewsAndDoWhen(reportDetail.fedBackAt != null, cardReportFeedback) {
            textReportFedBackBy.text = reportDetail.fedBackBy

            showViewsAndDoWhen(reportDetail.feedbackScore.isNotEmpty(),
                titleReportFeedbackScore,
                textReportFeedbackScore) {
                textReportFeedbackScore.text = reportDetail.feedbackScore
            }

            showViewsAndDoWhen(reportDetail.feedbackComment.isNotEmpty(),
                titleReportFeedbackComment,
                textReportFeedbackComment) {
                textReportFeedbackComment.text = reportDetail.feedbackComment
            }

            showViewsAndDoWhen(reportDetail.feedbackFile != null,
                iconReportFeedbackFile,
                textReportFeedbackFileName) {
                listOf(iconReportFeedbackFile, textReportFeedbackFileName).forEach {
                    it.setOnClickListener { view ->
                        if (reportDetail.feedbackFile!!.fileName.endsWith(".pdf")) {
                            startActivity(PdfActivity.intent(view.context,
                                reportDetail.feedbackFile!!.objectName))
                        }
                    }
                }

                textReportFeedbackFileName.text = reportDetail.feedbackFile!!.fileName
            }

            textReportFeedbackDate.text = TIME_FORMAT.format(reportDetail.fedBackAt!!)
        }
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
