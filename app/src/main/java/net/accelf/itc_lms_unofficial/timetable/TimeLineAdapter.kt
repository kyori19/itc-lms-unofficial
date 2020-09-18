package net.accelf.itc_lms_unofficial.timetable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.item_courses.view.*
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.models.Course
import net.accelf.itc_lms_unofficial.util.dp
import net.accelf.itc_lms_unofficial.util.set

class TimeLineAdapter(
    private val items: List<List<Course>>,
) : RecyclerView.Adapter<TimeLineAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_courses, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.apply {
            textPeriod.text = (position + 1).toString()

            cardCourse.apply {
                cardElevation = when (item.isEmpty()) {
                    true -> 0f.dp
                    false -> 3f.dp
                }
            }

            @Suppress("UNCHECKED_CAST")
            listCourses.set(
                item,
                CoursesAdapter::class.java as Class<RecyclerView.Adapter<RecyclerView.ViewHolder>>,
                true
            )
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardCourse: MaterialCardView = view.cardCourse
        val textPeriod: TextView = view.textPeriod
        val listCourses: RecyclerView = view.listCourses
    }
}
