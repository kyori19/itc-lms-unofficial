package net.accelf.itc_lms_unofficial.timetable

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_time_table.*
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.models.TimeTable
import net.accelf.itc_lms_unofficial.util.onSuccess

class TimeTableFragment : Fragment(R.layout.fragment_time_table) {

    private val viewModel by activityViewModels<TimeTableViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.timeTable.onSuccess(this) {
            if (viewPager.adapter == null) {
                viewPager.adapter = TimeLinesAdapter(this, it.courses)

                TabLayoutMediator(tabLayout, viewPager) { tab, index ->
                    tab.text = TimeTable.DayOfWeek.fromIndex(index).toString()
                }.attach()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): TimeTableFragment {
            return TimeTableFragment()
        }
    }
}
