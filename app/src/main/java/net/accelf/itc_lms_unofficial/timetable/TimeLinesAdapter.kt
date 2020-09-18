package net.accelf.itc_lms_unofficial.timetable

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import net.accelf.itc_lms_unofficial.models.Course

class TimeLinesAdapter(
    fragment: Fragment,
    private val items: List<List<List<Course?>>>,
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment {
        return TimeLineFragment.newInstance(items[position])
    }
}
