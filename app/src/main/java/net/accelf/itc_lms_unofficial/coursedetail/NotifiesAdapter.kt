package net.accelf.itc_lms_unofficial.coursedetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.models.Notify
import net.accelf.itc_lms_unofficial.util.timeSpanToString

class NotifiesAdapter(
    private val items: List<Notify>
) : RecyclerView.Adapter<NotifiesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notify, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.apply {
            textNotifyTitle.text = item.title
            textNotifyDate.apply {
                text = context.timeSpanToString(item.from, item.until)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textNotifyTitle: TextView = view.findViewById(R.id.textNotifyTitle)
        val textNotifyDate: TextView = view.findViewById(R.id.textNotifyDate)
    }
}
