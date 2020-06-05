package net.accelf.itc_lms_unofficial.timetable

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import net.accelf.itc_lms_unofficial.R
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

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textPeriod: TextView = view.findViewById(R.id.textPeriod)
        val cardCourse: MaterialCardView = view.findViewById(R.id.cardCourse)
        val textCourseName: TextView = view.findViewById(R.id.textCourseName)
        val textTeachersName: TextView = view.findViewById(R.id.textCourseTeachersName)
        val textCourseTemp: TextView = view.findViewById(R.id.textCourseTemp)
    }
}
