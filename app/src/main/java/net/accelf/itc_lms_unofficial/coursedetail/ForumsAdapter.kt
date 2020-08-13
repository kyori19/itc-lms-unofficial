package net.accelf.itc_lms_unofficial.coursedetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_forum.view.*
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.models.Forum
import net.accelf.itc_lms_unofficial.util.timeSpanToString

class ForumsAdapter(
    private val items: List<Forum>
) : RecyclerView.Adapter<ForumsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forum, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.apply {
            textForumTitle.text = item.title
            textForumDate.apply {
                text = context.timeSpanToString(item.from, item.until)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textForumTitle: TextView = view.textForumTitle
        val textForumDate: TextView = view.textForumDate
    }
}
