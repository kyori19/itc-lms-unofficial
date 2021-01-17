package net.accelf.itc_lms_unofficial.coursedetail

import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.databinding.ItemReportBinding
import net.accelf.itc_lms_unofficial.models.Report
import net.accelf.itc_lms_unofficial.reportdetail.ReportDetailActivity
import net.accelf.itc_lms_unofficial.util.UpdatableAdapter
import net.accelf.itc_lms_unofficial.util.timeSpanToString

class ReportsAdapter(
    private val courseId: String,
    items: List<Report>,
) : UpdatableAdapter<Report, ItemReportBinding>(items, ItemReportBinding::class.java) {

    override fun onBindViewHolder(holder: ViewHolder<ItemReportBinding>, position: Int) {
        val item = items[position]

        holder.binding.apply {
            root.setOnClickListener {
                it.context.startActivity(ReportDetailActivity.intent(it.context, courseId, item.id))
            }

            iconReportStatus.apply {
                setImageResource(
                    when (item.status) {
                        Report.ReportStatus.NOT_SUBMITTED -> R.drawable.ic_cancel
                        Report.ReportStatus.SUBMITTED_IN_TIME -> R.drawable.ic_check
                        Report.ReportStatus.SUBMITTED_AFTER_DEADLINE -> R.drawable.ic_time
                        Report.ReportStatus.TEMPORARILY_SAVED -> R.drawable.ic_save
                    }
                )
                contentDescription = context.getString(
                    when (item.status) {
                        Report.ReportStatus.NOT_SUBMITTED -> R.string.hint_icon_not_submitted
                        Report.ReportStatus.SUBMITTED_IN_TIME -> R.string.hint_icon_submitted_in_time
                        Report.ReportStatus.SUBMITTED_AFTER_DEADLINE -> R.string.hint_icon_submitted_after_deadline
                        Report.ReportStatus.TEMPORARILY_SAVED -> R.string.hint_icon_temporarily_saved
                    }
                )
            }

            titleReport.text = item.title
            textReportDate.apply {
                text = context.timeSpanToString(item.from, item.until)
            }
        }
    }
}
