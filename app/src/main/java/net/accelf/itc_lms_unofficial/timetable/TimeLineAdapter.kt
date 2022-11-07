package net.accelf.itc_lms_unofficial.timetable

import net.accelf.itc_lms_unofficial.databinding.ItemCoursesBinding
import net.accelf.itc_lms_unofficial.models.Course
import net.accelf.itc_lms_unofficial.util.UpdatableAdapter
import net.accelf.itc_lms_unofficial.util.dp
import net.accelf.itc_lms_unofficial.util.set

class TimeLineAdapter(
    items: List<List<Course>>,
) : UpdatableAdapter<List<Course>, ItemCoursesBinding>(items, ItemCoursesBinding::class.java) {

    override fun onBindViewHolder(holder: ViewHolder<ItemCoursesBinding>, position: Int) {
        val item = items[position]

        holder.binding.apply {
            // avoid false positive of TextView string concatenation
            val period = position + 1
            textPeriod.text = period.toString()

            cardCourse.apply {
                cardElevation = when (item.isEmpty()) {
                    true -> 0f.dp
                    false -> 3f.dp
                }
            }

            listCourses.set<Course, CoursesAdapter>(
                item,
                true
            )
        }
    }
}
