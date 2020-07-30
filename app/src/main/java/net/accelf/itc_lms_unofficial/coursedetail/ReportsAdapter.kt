package net.accelf.itc_lms_unofficial.coursedetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.models.Report
import net.accelf.itc_lms_unofficial.util.timeSpanToString

class ReportsAdapter(
    private val items: List<Report>
) : RecyclerView.Adapter<ReportsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.apply {
            iconReportStatus.apply {
                setImageResource(
                    when (item.status) {
                        Report.ReportStatus.NOT_SUBMITTED -> R.drawable.ic_cancel
                        Report.ReportStatus.SUBMITTED_IN_TIME -> R.drawable.ic_check
                        Report.ReportStatus.SUBMITTED_AFTER_DEADLINE -> R.drawable.ic_time
                    }
                )
                contentDescription = context.getString(
                    when (item.status) {
                        Report.ReportStatus.NOT_SUBMITTED -> R.string.hint_icon_not_submitted
                        Report.ReportStatus.SUBMITTED_IN_TIME -> R.string.hint_icon_submitted_in_time
                        Report.ReportStatus.SUBMITTED_AFTER_DEADLINE -> R.string.hint_icon_submitted_after_deadline
                    }
                )
            }

            titleReport.text = item.title
            textReportDate.apply {
                text = context.timeSpanToString(item.from, item.until)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconReportStatus: ImageView = view.findViewById(R.id.iconReportStatus)
        val titleReport: TextView = view.findViewById(R.id.titleReport)
        val textReportDate: TextView = view.findViewById(R.id.textReportDate)
    }
}
