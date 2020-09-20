package net.accelf.itc_lms_unofficial.timetable

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.fragment_time_table.*
import net.accelf.itc_lms_unofficial.MainActivity
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.models.SelectOption.Companion.selectedText
import net.accelf.itc_lms_unofficial.models.SelectOption.Companion.toTextStrings
import net.accelf.itc_lms_unofficial.models.SelectOption.Companion.valueFor
import net.accelf.itc_lms_unofficial.models.TimeTable
import net.accelf.itc_lms_unofficial.util.DATE_FORMAT
import net.accelf.itc_lms_unofficial.util.onSuccess
import net.accelf.itc_lms_unofficial.util.timeSpanToString

class TimeTableFragment : Fragment(R.layout.fragment_time_table) {

    private val viewModel by activityViewModels<TimeTableViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.timeTable.onSuccess(this) {
            val swipeRefresh = (activity as MainActivity).swipeRefresh

            if (viewPager.adapter == null) {
                viewPager.adapter = TimeLinesAdapter(this, it.courses)

                TabLayoutMediator(tabLayout, viewPager) { tab, index ->
                    tab.text = TimeTable.DayOfWeek.fromIndex(index).toString()
                }.attach()

                viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageScrollStateChanged(state: Int) {
                        super.onPageScrollStateChanged(state)

                        when (state) {
                            ViewPager2.SCROLL_STATE_DRAGGING -> {
                                swipeRefresh.isEnabled = false
                            }
                            ViewPager2.SCROLL_STATE_IDLE,
                            ViewPager2.SCROLL_STATE_SETTLING,
                            -> {
                                swipeRefresh.isEnabled = true
                            }
                        }
                    }
                })
            }

            if (pickerYear.adapter == null) {
                pickerYear.setAdapter(ArrayAdapter(requireContext(),
                    R.layout.item_picker_text,
                    it.years.toTextStrings()))
            } else {
                @Suppress("UNCHECKED_CAST")
                (pickerYear.adapter as ArrayAdapter<String>).apply {
                    clear()
                    addAll(it.years.toTextStrings())
                }
            }
            pickerYear.setText(it.years.selectedText(), false)

            if (pickerTerm.adapter == null) {
                pickerTerm.setAdapter(ArrayAdapter(requireContext(),
                    R.layout.item_picker_text,
                    it.terms.toTextStrings()))

                listOf(pickerYear, pickerTerm).forEach { picker ->
                    picker.setOnItemClickListener { _, _, _, _ ->
                        swipeRefresh.isRefreshing = true
                        viewModel.load(
                            it.years.valueFor(pickerYear.text.toString()),
                            it.terms.valueFor(pickerTerm.text.toString()),
                        )
                    }
                }
            } else {
                @Suppress("UNCHECKED_CAST")
                (pickerTerm.adapter as ArrayAdapter<String>).apply {
                    clear()
                    addAll(it.terms.toTextStrings())
                }
            }
            pickerTerm.setText(it.terms.selectedText(), false)

            textTimeTableDate.text = when (it.until == null) {
                true -> DATE_FORMAT.format(it.since)
                false -> requireContext().timeSpanToString(it.since, it.until, false)
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
