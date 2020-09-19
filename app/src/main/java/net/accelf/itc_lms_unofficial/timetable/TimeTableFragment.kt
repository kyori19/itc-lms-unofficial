package net.accelf.itc_lms_unofficial.timetable

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.fragment_time_table.*
import net.accelf.itc_lms_unofficial.MainActivity
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.models.SelectOption.Companion.selected
import net.accelf.itc_lms_unofficial.models.SelectOption.Companion.toStrings
import net.accelf.itc_lms_unofficial.models.SelectOption.Companion.valueFor
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

            if (pickerYear.adapter == null) {
                pickerYear.setAdapter(ArrayAdapter(requireContext(),
                    R.layout.item_picker_text,
                    it.years.toStrings()))
            } else {
                @Suppress("UNCHECKED_CAST")
                (pickerYear.adapter as ArrayAdapter<String>).apply {
                    clear()
                    addAll(it.years.toStrings())
                }
            }
            pickerYear.setText(it.years.selected(), false)

            if (pickerTerm.adapter == null) {
                pickerTerm.setAdapter(ArrayAdapter(requireContext(),
                    R.layout.item_picker_text,
                    it.terms.toStrings()))

                listOf(pickerYear, pickerTerm).forEach { picker ->
                    picker.setOnItemClickListener { _, _, _, _ ->
                        (activity as MainActivity).swipeRefresh.isRefreshing = true
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
                    addAll(it.terms.toStrings())
                }
            }
            pickerTerm.setText(it.terms.selected(), false)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): TimeTableFragment {
            return TimeTableFragment()
        }
    }
}
