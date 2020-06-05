package net.accelf.itc_lms_unofficial.timetable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.models.Course
import java.io.Serializable

const val ARG_TIME_LINE = "time_line"

class TimeLineFragment : Fragment() {

    private lateinit var timeLine: List<Course?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            @Suppress("UNCHECKED_CAST")
            timeLine = it.getSerializable(ARG_TIME_LINE) as List<Course?>
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_time_line, container, false)

        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = CoursesAdapter(timeLine)
            }
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(timeLine: List<Course?>): TimeLineFragment {
            return TimeLineFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_TIME_LINE, timeLine as Serializable)
                }
            }
        }
    }
}
