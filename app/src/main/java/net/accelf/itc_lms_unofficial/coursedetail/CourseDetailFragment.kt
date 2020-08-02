package net.accelf.itc_lms_unofficial.coursedetail

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_course_detail.*
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.models.CourseDetail
import net.accelf.itc_lms_unofficial.util.fromHtml
import net.accelf.itc_lms_unofficial.util.set

private const val ARG_COURSE_DETAIL = "course_detail"

class CourseDetailFragment : Fragment(R.layout.fragment_course_detail) {
    private lateinit var courseDetail: CourseDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            courseDetail = it.getSerializable(ARG_COURSE_DETAIL) as CourseDetail
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textDepartment.text = courseDetail.department
        textCourseName.text = courseDetail.name
        textCourseCode.text = courseDetail.courseCode
        textPeriod.text = getString(
            R.string.text_period,
            courseDetail.semester, courseDetail.dow.toString(), courseDetail.period
        )

        textTeachersName.text = courseDetail.teachers.joinToString(", ")
        textCourseSummary.apply {
            text = courseDetail.summary.fromHtml()
            movementMethod = LinkMovementMethod.getInstance()
        }

        @Suppress("UNCHECKED_CAST")
        listNotifies.set(
            courseDetail.notifies,
            NotifiesAdapter::class.java as Class<RecyclerView.Adapter<RecyclerView.ViewHolder>>,
            headerNotifies
        )

        @Suppress("UNCHECKED_CAST")
        listCourseContents.set(
            courseDetail.courseContents,
            CourseContentsAdapter::class.java as Class<RecyclerView.Adapter<RecyclerView.ViewHolder>>,
            headerCourseContents
        )

        @Suppress("UNCHECKED_CAST")
        listReports.set(
            courseDetail.reports,
            ReportsAdapter::class.java as Class<RecyclerView.Adapter<RecyclerView.ViewHolder>>,
            headerReports
        )

        @Suppress("UNCHECKED_CAST")
        listMessages.set(
            courseDetail.messages,
            MessagesAdapter::class.java as Class<RecyclerView.Adapter<RecyclerView.ViewHolder>>,
            headerMessages
        )

        @Suppress("UNCHECKED_CAST")
        listAttendances.set(
            courseDetail.attendances,
            AttendancesAdapter::class.java as Class<RecyclerView.Adapter<RecyclerView.ViewHolder>>,
            headerAttendances
        )

        @Suppress("UNCHECKED_CAST")
        listTests.set(
            courseDetail.tests,
            TestsAdapter::class.java as Class<RecyclerView.Adapter<RecyclerView.ViewHolder>>,
            headerTests
        )

        @Suppress("UNCHECKED_CAST")
        listForums.set(
            courseDetail.forums,
            ForumsAdapter::class.java as Class<RecyclerView.Adapter<RecyclerView.ViewHolder>>,
            headerForums
        )

        @Suppress("UNCHECKED_CAST")
        listSurveys.set(
            courseDetail.surveys,
            SurveysAdapter::class.java as Class<RecyclerView.Adapter<RecyclerView.ViewHolder>>,
            headerSurveys
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(courseDetail: CourseDetail): CourseDetailFragment {
            return CourseDetailFragment()
                .apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_COURSE_DETAIL, courseDetail)
                    }
                }
        }
    }
}
