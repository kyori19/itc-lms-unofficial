package net.accelf.itc_lms_unofficial.timetable

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.item_course.view.*
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.coursedetail.CourseDetailActivity
import net.accelf.itc_lms_unofficial.models.Course
import net.accelf.itc_lms_unofficial.util.dp

class CoursesAdapter(
    private val items: List<Course?>
) : RecyclerView.Adapter<CoursesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.textPeriod.text = (position + 1).toString()

        if (item != null) {
            holder.apply {
                root.setOnClickListener { view ->
                    view.context.startActivity(CourseDetailActivity.intent(view.context, item.id))
                }
                cardCourse.apply {
                    cardElevation = 1f.dp
                    strokeWidth = if (item.temp) {
                        1.dp
                    } else {
                        0.dp
                    }
                }
                textCourseName.apply {
                    visibility = VISIBLE
                    text = item.name
                }
                textTeachersName.apply {
                    visibility = VISIBLE
                    text = item.teachers.joinToString(", ")
                }
                textCourseTemp.visibility = if (item.temp) {
                    VISIBLE
                } else {
                    GONE
                }
            }
        } else {
            holder.apply {
                root.setOnClickListener {}
                cardCourse.apply {
                    cardElevation = 0f.dp
                    strokeWidth = 0.dp
                }
                textCourseName.apply {
                    visibility = GONE
                    text = ""
                }
                textTeachersName.apply {
                    visibility = GONE
                    text = ""
                }
                textCourseTemp.visibility = GONE
            }
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root: View = view
        val textPeriod: TextView = view.textPeriod
        val cardCourse: MaterialCardView = view.cardCourse
        val textCourseName: TextView = view.textCourseName
        val textTeachersName: TextView = view.textCourseTeachersName
        val textCourseTemp: TextView = view.textCourseTemp
    }
}
