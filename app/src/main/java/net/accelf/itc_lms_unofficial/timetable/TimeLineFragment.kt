package net.accelf.itc_lms_unofficial.timetable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.util.onSuccess

class TimeLineFragment : Fragment() {

    private val viewModel by activityViewModels<TimeTableViewModel>()
    private var viewModelPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModelPosition = it.getInt(ARG_VIEW_MODEL_POSITION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_time_line, container, false)

        viewModel.timeTable.onSuccess(this) {
            if ((view as RecyclerView).layoutManager == null) {
                view.layoutManager = LinearLayoutManager(context)
            }
            if (view.adapter == null) {
                view.adapter = TimeLineAdapter(it.courses[viewModelPosition])
            } else {
                (view.adapter as TimeLineAdapter).items = it.courses[viewModelPosition]
            }
        }
        return view
    }

    companion object {
        private const val ARG_VIEW_MODEL_POSITION = "view_model_position"

        @JvmStatic
        fun newInstance(viewModelPosition: Int): TimeLineFragment {
            return TimeLineFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_VIEW_MODEL_POSITION, viewModelPosition)
                }
            }
        }
    }
}
