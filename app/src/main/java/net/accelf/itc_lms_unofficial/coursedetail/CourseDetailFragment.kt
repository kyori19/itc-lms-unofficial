package net.accelf.itc_lms_unofficial.coursedetail

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tingyik90.snackprogressbar.SnackProgressBar
import com.tingyik90.snackprogressbar.SnackProgressBarManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_course_detail.*
import net.accelf.itc_lms_unofficial.PdfActivity
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.models.CourseDetail
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.util.fromHtml
import net.accelf.itc_lms_unofficial.util.set
import net.accelf.itc_lms_unofficial.util.setWithoutInitAdapter
import net.accelf.itc_lms_unofficial.util.withResponse
import java.util.*
import javax.inject.Inject

private const val ARG_COURSE_DETAIL = "course_detail"

@AndroidEntryPoint
class CourseDetailFragment : Fragment(R.layout.fragment_course_detail), MaterialListener {

    private lateinit var courseDetail: CourseDetail
    private var loadingFileId = false

    @Inject
    lateinit var lms: LMS

    private val snackProgressBarManager by lazy {
        SnackProgressBarManager(requireView(), this)
            .useRoundedCornerBackground(true)
    }

    private val snackProgressBar by lazy {
        SnackProgressBar(
            SnackProgressBar.TYPE_HORIZONTAL,
            getString(R.string.snackbar_getting_file_id)
        )
            .setIsIndeterminate(true)
            .setAllowUserInput(true)
    }

    private val customTabsIntent by lazy {
        CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setToolbarColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            .build()
    }

    private val openingLinkDialog by lazy {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_title_open_link)
            .setNeutralButton(R.string.button_dialog_close) { dialog, _ ->
                dialog.dismiss()
            }
    }

    private val openingVideoDialog by lazy {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_title_open_video)
            .setMessage(R.string.dialog_message_open_video_unsupported)
            .setPositiveButton(R.string.button_dialog_close) { dialog, _ ->
                dialog.dismiss()
            }
    }

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

        listCourseContents.setWithoutInitAdapter(
            courseDetail.courseContents,
            headerCourseContents
        ) {
            CourseContentsAdapter(courseDetail.courseContents, this)
        }

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

    override fun openFile(
        materialId: String,
        resourceId: String,
        fileName: String,
        objectName: String,
        endDate: Date
    ) {
        if (loadingFileId) {
            Toast.makeText(requireContext(), R.string.toast_already_downloading, Toast.LENGTH_SHORT)
                .show()
            return
        }

        loadingFileId = true
        snackProgressBarManager.show(snackProgressBar, SnackProgressBarManager.LENGTH_INDEFINITE)
        lms.getFileId(courseDetail.id, materialId, resourceId, fileName, objectName)
            .withResponse(activity as AppCompatActivity) {
                loadingFileId = false
                snackProgressBarManager.dismiss()

                if (fileName.endsWith(".pdf")) {
                    startActivity(PdfActivity.intent(requireContext(), it, materialId, endDate))
                }
            }
    }

    override fun openLink(url: String) {
        openingLinkDialog.setMessage(getString(R.string.dialog_message_open_link, url))
            .setPositiveButton(R.string.button_dialog_open) { _, _ ->
                customTabsIntent.launchUrl(requireContext(), url.toUri())
            }
            .show()
    }

    override fun openVideo() {
        openingVideoDialog.show()
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
