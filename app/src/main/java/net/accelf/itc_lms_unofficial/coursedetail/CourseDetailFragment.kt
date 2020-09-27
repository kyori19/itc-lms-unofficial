package net.accelf.itc_lms_unofficial.coursedetail

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.tingyik90.snackprogressbar.SnackProgressBar
import com.tingyik90.snackprogressbar.SnackProgressBarManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_course_detail.*
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.di.CustomLinkMovementMethod
import net.accelf.itc_lms_unofficial.file.Downloadable
import net.accelf.itc_lms_unofficial.models.*
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CourseDetailFragment : Fragment(R.layout.fragment_course_detail), NotifyListener,
    MaterialListener {

    private lateinit var courseId: String

    @Inject
    lateinit var lms: LMS

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var linkMovementMethod: CustomLinkMovementMethod

    private val viewModel by activityViewModels<CourseDetailViewModel>()

    private val snackProgressBarManager by lazy {
        SnackProgressBarManager(requireView(), this)
            .useRoundedCornerBackground(true)
    }

    private val notifySnackProgressBar by lazy {
        SnackProgressBar(
            SnackProgressBar.TYPE_HORIZONTAL,
            getString(R.string.snackbar_fetching_notify)
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

    private val notifyDialog by lazy {
        MaterialAlertDialogBuilder(requireContext())
            .setPositiveButton(R.string.button_dialog_close) { dialog, _ ->
                dialog.dismiss()
            }
            .setOnDismissListener {
                viewModel.closeNotify()
            }
            .create()
            .apply {
                setOnShowListener {
                    (it as AlertDialog).findViewById<TextView>(android.R.id.message)
                        ?.apply {
                            movementMethod = linkMovementMethod
                        }
                }
            }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.courseDetail.onSuccess(viewLifecycleOwner) { courseDetail ->
            courseId = courseDetail.id

            textDepartment.text = courseDetail.department
            textCourseName.text = courseDetail.name
            textCourseCode.text = courseDetail.courseCode
            textPeriod.text = StringBuilder(getString(
                R.string.text_period,
                courseDetail.semester,
                courseDetail.periods.first().first,
                courseDetail.periods.first().second
            )).apply {
                courseDetail.periods.filterIndexed { index, _ -> index != 0 }
                    .forEach {
                        append(", ${it.first} ${it.second}")
                    }
            }

            textTeachersName.text = courseDetail.teachers.joinToString(", ")
            textCourseSummary.apply {
                text = courseDetail.summary.fromHtml()
                movementMethod = linkMovementMethod
            }
            showViewsAndDoWhen(courseDetail.onlineInfo != null,
                titleOnlineInfo,
                textOnlineInfo,
                textOnlineInfoDate) {
                textOnlineInfoDate.text =
                    courseDetail.onlineInfoUpdatedAt?.let { TIME_FORMAT.format(it) }
                textOnlineInfo.apply {
                    text = courseDetail.onlineInfo!!.toSpanned()
                    movementMethod = linkMovementMethod
                }
            }

            listNotifies.setWithoutInitAdapter(
                courseDetail.notifies,
                headerNotifies
            ) {
                NotifiesAdapter(courseDetail.notifies, this)
            }

            listCourseContents.setWithoutInitAdapter(
                courseDetail.courseContents,
                headerCourseContents
            ) {
                CourseContentsAdapter(courseDetail.courseContents, this)
            }

            listReports.setWithoutInitAdapter(
                courseDetail.reports,
                headerReports
            ) {
                ReportsAdapter(courseDetail.id, courseDetail.reports)
            }

            listMessages.set<Message, MessagesAdapter>(
                courseDetail.messages,
                headerMessages
            )

            listAttendances.set<Attendance, AttendancesAdapter>(
                courseDetail.attendances,
                headerAttendances
            )

            listTests.set<Test, TestsAdapter>(
                courseDetail.tests,
                headerTests
            )

            listForums.set<Forum, ForumsAdapter>(
                courseDetail.forums,
                headerForums
            )

            listSurveys.set<Survey, SurveysAdapter>(
                courseDetail.surveys,
                headerSurveys
            )
        }

        viewModel.notifyDetail.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            }

            when (it) {
                is Loading -> {
                    snackProgressBarManager.show(
                        notifySnackProgressBar,
                        SnackProgressBarManager.LENGTH_INDEFINITE
                    )
                }
                is Success -> {
                    snackProgressBarManager.dismiss()

                    notifyDialog.apply {
                        setTitle(it.data.title)
                        setMessage(it.data.text.fromHtml().autoLink())
                        show()
                    }
                }
            }
        }
    }

    override fun openNotify(notifyId: String) {
        if (!viewModel.loadNotify(courseId, notifyId)) {
            Toast.makeText(requireContext(), R.string.toast_already_fetching, Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun openFile(material: Material) {
        val downloadable = Downloadable.materialFile(courseId, material)
        downloadable.open(this, gson)
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
        fun newInstance(): CourseDetailFragment {
            return CourseDetailFragment()
        }
    }
}
