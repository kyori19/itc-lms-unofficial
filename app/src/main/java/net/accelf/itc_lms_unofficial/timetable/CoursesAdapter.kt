package net.accelf.itc_lms_unofficial.timetable

import android.view.View.GONE
import android.view.View.VISIBLE
import net.accelf.itc_lms_unofficial.coursedetail.CourseDetailActivity
import net.accelf.itc_lms_unofficial.databinding.ItemCourseBinding
import net.accelf.itc_lms_unofficial.models.Course
import net.accelf.itc_lms_unofficial.util.UpdatableAdapter

class CoursesAdapter(
    items: List<Course>,
) : UpdatableAdapter<Course, ItemCourseBinding>(items, ItemCourseBinding::class.java) {

    override fun onBindViewHolder(holder: ViewHolder<ItemCourseBinding>, position: Int) {
        val item = items[position]

        holder.binding.apply {
            root.setOnClickListener { view ->
                view.context.startActivity(CourseDetailActivity.intent(view.context, item.id))
            }
            textCourseName.apply {
                visibility = VISIBLE
                text = item.name
            }
            textCourseTeachersName.apply {
                visibility = VISIBLE
                text = item.teachers.joinToString(", ")
            }
            textCourseTemp.visibility = if (item.temp) {
                VISIBLE
            } else {
                GONE
            }
        }
    }
}
