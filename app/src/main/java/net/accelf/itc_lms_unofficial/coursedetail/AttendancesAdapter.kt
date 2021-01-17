package net.accelf.itc_lms_unofficial.coursedetail

import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.databinding.ItemAttendanceBinding
import net.accelf.itc_lms_unofficial.models.Attendance
import net.accelf.itc_lms_unofficial.util.DATE_FORMAT
import net.accelf.itc_lms_unofficial.util.UpdatableAdapter

class AttendancesAdapter(
    items: List<Attendance>,
) : UpdatableAdapter<Attendance, ItemAttendanceBinding>(items, ItemAttendanceBinding::class.java) {

    override fun onBindViewHolder(holder: ViewHolder<ItemAttendanceBinding>, position: Int) {
        val item = items[position]

        holder.binding.apply {
            iconAttendanceStatus.apply {
                setImageResource(
                    when (item.status) {
                        Attendance.AttendanceStatus.PRESENT -> R.drawable.ic_check
                        Attendance.AttendanceStatus.LATE -> R.drawable.ic_time
                        Attendance.AttendanceStatus.ABSENT -> R.drawable.ic_none
                        else -> R.drawable.ic_cancel
                    }
                )
                contentDescription = context.getString(
                    when (item.status) {
                        Attendance.AttendanceStatus.PRESENT -> R.string.hint_icon_present
                        Attendance.AttendanceStatus.LATE -> R.string.hint_icon_late
                        Attendance.AttendanceStatus.ABSENT -> R.string.hint_icon_absent
                        else -> R.string.hint_icon_unknown
                    }
                )
            }

            textAttendanceDate.text = item.date?.let { DATE_FORMAT.format(it) }
        }
    }
}
