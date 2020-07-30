package net.accelf.itc_lms_unofficial.coursedetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.models.CourseContent
import net.accelf.itc_lms_unofficial.util.fromHtml
import net.accelf.itc_lms_unofficial.util.set
import net.accelf.itc_lms_unofficial.util.timeSpanToString
import net.accelf.itc_lms_unofficial.view.ExpandableHeaderView

class CourseContentsAdapter(
    private val items: List<CourseContent>
) : RecyclerView.Adapter<CourseContentsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.apply {
            titleCourseContent.text = item.title
            textCourseContentDate.apply {
                text = context.timeSpanToString(item.from, item.until)
            }
            textCourseContentSummary.text = item.summary.fromHtml()

            @Suppress("UNCHECKED_CAST")
            listMaterials.set(
                item.materials,
                MaterialsAdapter::class.java as Class<RecyclerView.Adapter<RecyclerView.ViewHolder>>,
                headerMaterials
            )
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleCourseContent: TextView = view.findViewById(R.id.titleCourseContent)
        val textCourseContentDate: TextView = view.findViewById(R.id.textCourseContentDate)
        val textCourseContentSummary: TextView = view.findViewById(R.id.textCourseContentSummary)
        val headerMaterials: ExpandableHeaderView = view.findViewById(R.id.headerMaterials)
        val listMaterials: RecyclerView = view.findViewById(R.id.listMaterials)
    }
}
