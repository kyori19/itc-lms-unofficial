package net.accelf.itc_lms_unofficial.timetable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_time_table.*
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.models.TimeTable

private const val ARG_TIME_TABLE = "time_table"

class TimeTableFragment : Fragment() {

    private lateinit var timeTable: TimeTable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            timeTable = it.getSerializable(ARG_TIME_TABLE) as TimeTable
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_time_table, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager.adapter = TimeLinesAdapter(this, timeTable.courses)

        TabLayoutMediator(tabLayout, viewPager) { tab, index ->
            tab.text = TimeTable.DayOfWeek.fromIndex(index).toString()
        }.attach()
    }

    companion object {
        @JvmStatic
        fun newInstance(timeTable: TimeTable): TimeTableFragment {
            return TimeTableFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_TIME_TABLE, timeTable)
                }
            }
        }
    }
}
