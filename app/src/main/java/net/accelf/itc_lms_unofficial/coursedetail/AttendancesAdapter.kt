package net.accelf.itc_lms_unofficial.coursedetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_attendance.view.*
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.models.Attendance
import net.accelf.itc_lms_unofficial.models.DATE_FORMAT

class AttendancesAdapter(
    private val items: List<Attendance>
) : RecyclerView.Adapter<AttendancesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attendance, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.apply {
            iconAttendanceStatus.apply {
                setImageResource(
                    when (item.status) {
                        Attendance.AttendanceStatus.PRESENT -> R.drawable.ic_check
                        Attendance.AttendanceStatus.ABSENT -> R.drawable.ic_cancel
                    }
                )
                contentDescription = context.getString(
                    when (item.status) {
                        Attendance.AttendanceStatus.PRESENT -> R.string.hint_icon_present
                        Attendance.AttendanceStatus.ABSENT -> R.string.hint_icon_absent
                    }
                )
            }

            textAttendanceDate.text = item.date?.let { DATE_FORMAT.format(it) }
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconAttendanceStatus: ImageView = view.iconAttendanceStatus
        val textAttendanceDate: TextView = view.textAttendanceDate
    }
}
