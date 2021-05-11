package net.accelf.itc_lms_unofficial.coursedetail

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.tingyik90.snackprogressbar.SnackProgressBar
import com.tingyik90.snackprogressbar.SnackProgressBarManager
import dagger.hilt.android.AndroidEntryPoint
import net.accelf.itc_lms_unofficial.BaseFragment
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.coursedetail.SendAttendanceDialogFragment.Companion.BUNDLE_RESULT
import net.accelf.itc_lms_unofficial.databinding.FragmentCourseDetailBinding
import net.accelf.itc_lms_unofficial.di.CustomLinkMovementMethod
import net.accelf.itc_lms_unofficial.file.download.Downloadable
import net.accelf.itc_lms_unofficial.file.download.Downloadable.Companion.preparePermissionRequestForDownloadable
import net.accelf.itc_lms_unofficial.models.*
import net.accelf.itc_lms_unofficial.permission.PermissionRequestable
import net.accelf.itc_lms_unofficial.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CourseDetailFragment :
    BaseFragment<FragmentCourseDetailBinding>(FragmentCourseDetailBinding::class.java),
    NotifyListener, MaterialListener, PermissionRequestable, Downloadable.ProvidesGson {

    @Inject
    override lateinit var gson: Gson

    @Inject
    lateinit var linkMovementMethod: CustomLinkMovementMethod

    private val viewModel by activityViewModels<CourseDetailViewModel>()

    private var downloadable: Downloadable? = null
    override var permissionRequestLauncher: ActivityResultLauncher<String> =
        preparePermissionRequestForDownloadable {
            val d = downloadable!!
            downloadable = null
            d
        }

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

    private val attendanceSendSnackProgressBar by lazy {
        SnackProgressBar(
            SnackProgressBar.TYPE_HORIZONTAL,
            getString(R.string.snackbar_fetching_attendance_send)
        )
            .setIsIndeterminate(true)
            .setAllowUserInput(true)
    }

    private val customTabsIntent by lazy {
        CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setDefaultColorSchemeParams(
                CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                    .build()
            )
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

    private val sendAttendanceSuccessDialog by lazy {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_title_send_attendance_success)
            .setMessage(R.string.dialog_message_send_attendance_success)
            .setPositiveButton(R.string.button_dialog_close) { dialog, _ ->
                dialog.dismiss()
            }
            .setOnDismissListener {
                viewModel.closeSendAttendance()
                viewModel.load()
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.courseDetail.onSuccess(viewLifecycleOwner) { courseDetail ->
            binding.textDepartment.text = courseDetail.department
            binding.textCourseName.text = courseDetail.name
            binding.textCourseCode.text = courseDetail.courseCode
            binding.textPeriod.text = StringBuilder(getString(
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

            showViewsAndDoWhen(courseDetail.sendAttendanceId != null,
                binding.buttonSendAttendance) {
                binding.buttonSendAttendance.setOnClickListener {
                    if (!viewModel.prepareForSendingAttendance(courseDetail.sendAttendanceId!!)) {
                        Toast.makeText(requireContext(),
                            R.string.toast_already_fetching,
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            binding.textTeachersName.text = courseDetail.teachers.joinToString(", ")
            binding.textCourseSummary.apply {
                text = courseDetail.summary.fromHtml()
                movementMethod = linkMovementMethod
            }
            showViewsAndDoWhen(courseDetail.onlineInfo != null,
                binding.titleOnlineInfo,
                binding.textOnlineInfo,
                binding.textOnlineInfoDate) {
                binding.textOnlineInfoDate.text =
                    courseDetail.onlineInfoUpdatedAt?.let { TIME_FORMAT.format(it) }
                binding.textOnlineInfo.apply {
                    text = courseDetail.onlineInfo!!.toSpanned()
                    movementMethod = linkMovementMethod
                }
            }

            binding.listNotifies.setWithoutInitAdapter(
                courseDetail.notifies,
                binding.headerNotifies
            ) {
                NotifiesAdapter(courseDetail.notifies, this)
            }

            viewModel.focusCourseContentResourceId?.let {
                binding.expandableCourseContents.isExpanded = true
            }
            binding.listCourseContents.setWithoutInitAdapter(
                courseDetail.courseContents,
                binding.headerCourseContents
            ) {
                CourseContentsAdapter(courseDetail.courseContents, this, viewModel)
            }

            binding.listReports.setWithoutInitAdapter(
                courseDetail.reports,
                binding.headerReports
            ) {
                ReportsAdapter(courseDetail.id, courseDetail.reports)
            }

            binding.listMessages.set<Message, MessagesAdapter>(
                courseDetail.messages,
                binding.headerMessages
            )

            binding.listAttendances.set<Attendance, AttendancesAdapter>(
                courseDetail.attendances,
                binding.headerAttendances
            )

            binding.listTests.set<Test, TestsAdapter>(
                courseDetail.tests,
                binding.headerTests
            )

            binding.listForums.set<Forum, ForumsAdapter>(
                courseDetail.forums,
                binding.headerForums
            )

            binding.listSurveys.set<Survey, SurveysAdapter>(
                courseDetail.surveys,
                binding.headerSurveys
            )
        }

        viewModel.notifyDetail.withSnackProgressBar(viewLifecycleOwner,
            notifySnackProgressBar,
            snackProgressBarManager,
            { viewModel.closeNotify() }) {
            notifyDialog.apply {
                setTitle(it.title)
                setMessage(it.text.fromHtml().autoLink())
                show()
            }
        }

        viewModel.attendanceSend.withSnackProgressBar(viewLifecycleOwner,
            attendanceSendSnackProgressBar,
            snackProgressBarManager,
            { viewModel.closeSendAttendance() }) {
            if (it.success) {
                sendAttendanceSuccessDialog.show()
                return@withSnackProgressBar
            }

            SendAttendanceDialogFragment.newInstance()
                .show(parentFragmentManager, SendAttendanceDialogFragment::class.java.simpleName)
        }

        setFragmentResultListener(SendAttendanceDialogFragment::class.java.simpleName) { _, it ->
            @Suppress("UNCHECKED_CAST")
            (it.getSerializable(BUNDLE_RESULT) as Result<SendAttendanceDialogResult>).onSuccess {
                if (!viewModel.sendAttendance(it.attendancePassword, it.attendanceComment)) {
                    Toast.makeText(requireContext(),
                        R.string.toast_already_fetching,
                        Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun openNotify(notifyId: String) {
        if (!viewModel.loadNotify(notifyId)) {
            Toast.makeText(requireContext(), R.string.toast_already_fetching, Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun openFile(material: Material) {
        downloadable = Downloadable.materialFile(viewModel.courseId, material)
        downloadable!!.open(this)
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
