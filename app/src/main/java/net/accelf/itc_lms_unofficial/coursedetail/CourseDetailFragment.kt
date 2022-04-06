package net.accelf.itc_lms_unofficial.coursedetail

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.tingyik90.snackprogressbar.SnackProgressBar
import com.tingyik90.snackprogressbar.SnackProgressBarManager
import dagger.hilt.android.AndroidEntryPoint
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.di.CustomLinkMovementMethod
import net.accelf.itc_lms_unofficial.file.download.Downloadable
import net.accelf.itc_lms_unofficial.file.download.Downloadable.Companion.preparePermissionRequestForDownloadable
import net.accelf.itc_lms_unofficial.models.CourseDetail
import net.accelf.itc_lms_unofficial.models.Material
import net.accelf.itc_lms_unofficial.permission.PermissionRequestable
import net.accelf.itc_lms_unofficial.reportdetail.ReportDetailActivity
import net.accelf.itc_lms_unofficial.ui.*
import net.accelf.itc_lms_unofficial.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CourseDetailFragment : Fragment(), PermissionRequestable, Downloadable.ProvidesGson {

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

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return compose {
            val courseDetailRequest by viewModel.courseDetail.observeAsState()

            when (courseDetailRequest) {
                is Success -> {
                    CourseDetailFragmentContent(
                        courseDetail = (courseDetailRequest as Success).data,
                        linkMovementMethod = linkMovementMethod,
                        focusCourseContentResourceId = viewModel.focusCourseContentResourceId,
                        openDescription = viewModel.openDescription,
                    )
                }
                else -> {
                }
            }
        }
    }

    @ExperimentalComposeUiApi
    @Composable
    @Preview
    private fun PreviewCourseDetailFragmentContent() {
        CourseDetailFragmentContent(
            courseDetail = CourseDetail.sample,
            linkMovementMethod = CustomLinkMovementMethod(),
            focusCourseContentResourceId = Material.sampleFile.materialId,
            openDescription = true,
        )
    }

    @ExperimentalComposeUiApi
    @Composable
    private fun CourseDetailFragmentContent(
        courseDetail: CourseDetail,
        linkMovementMethod: CustomLinkMovementMethod,
        focusCourseContentResourceId: String? = null,
        openDescription: Boolean = false,
    ) {
        val context = LocalContext.current

        Column(
            modifier = Modifier
                .padding(Values.Spacing.around)
                .verticalScroll(rememberScrollState()),
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .padding(Values.Spacing.around)
                    .fillMaxWidth(),
            ) {
                val (textDepartment, textPeriod, textCourseTitle) = createRefs()

                NormalText(
                    text = courseDetail.department,
                    modifier = Modifier
                        .padding(Values.Spacing.around)
                        .constrainAs(textDepartment) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            bottom.linkTo(textCourseTitle.top)
                        },
                )
                NormalText(
                    text = stringResource(
                        R.string.text_period,
                        courseDetail.semester,
                        courseDetail.periods.joinToString(", ") { (dow, p) -> "$dow $p" },
                    ),
                    modifier = Modifier
                        .padding(Values.Spacing.around)
                        .constrainAs(textPeriod) {
                            top.linkTo(parent.top)
                            bottom.linkTo(textCourseTitle.top)
                            end.linkTo(parent.end)
                        },
                )
                NormalText(
                    text = AnnotatedString.Builder()
                        .apply {
                            pushStyle(SpanStyle(
                                color = MaterialTheme.colors.secondary,
                                fontSize = MaterialTheme.typography.h5.fontSize,
                            ))
                            append(courseDetail.name)
                            pop()
                            append(" ")
                            append(courseDetail.courseCode)
                        }.toAnnotatedString(),
                    modifier = Modifier
                        .padding(Values.Spacing.around)
                        .constrainAs(textCourseTitle) {
                            top.linkTo(textDepartment.bottom)
                            start.linkTo(parent.start)
                            bottom.linkTo(parent.bottom)
                        },
                )
            }

            courseDetail.sendAttendanceId?.let { sendAttendanceId ->
                Button(
                    onClick = {
                        if (!viewModel.prepareForSendingAttendance(sendAttendanceId)) {
                            Toast.makeText(context,
                                R.string.toast_already_fetching,
                                Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    modifier = Modifier
                        .padding(Values.Spacing.around)
                        .fillMaxWidth(),
                ) {
                    Text(stringResource(id = R.string.button_send_attendance))
                }

                val attendanceSendRequest by viewModel.attendanceSend.observeAsState()
                if (attendanceSendRequest != null && attendanceSendRequest is Success) {
                    val attendanceSend = (attendanceSendRequest as Success).data
                    if (!attendanceSend.success) {
                        SendAttendanceDialog(
                            attendanceSend = attendanceSend,
                            onSubmit = { password, comment ->
                                if (!viewModel.sendAttendance(password, comment)) {
                                    Toast.makeText(context,
                                        R.string.toast_already_fetching,
                                        Toast.LENGTH_SHORT)
                                        .show()
                                }
                            },
                            onCancel = { viewModel.closeSendAttendance() },
                        )
                    }
                }
            }

            ExpandableCard(
                title = stringResource(id = R.string.title_course_details),
                modifier = Modifier.padding(Values.Spacing.around),
                defaultExpanded = openDescription,
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        NormalText(
                            text = stringResource(R.string.title_teachers_name),
                            modifier = Modifier
                                .padding(Values.Spacing.around)
                                .weight(1f),
                        )

                        NormalText(
                            text = courseDetail.teachers.joinToString(", "),
                            modifier = Modifier.padding(Values.Spacing.around),
                            style = MaterialTheme.typography.h6,
                        )
                    }

                    NormalText(
                        text = stringResource(R.string.title_course_summary),
                        modifier = Modifier.padding(Values.Spacing.around),
                    )

                    SpannedText(
                        text = courseDetail.summary.fromHtml(),
                        modifier = Modifier.padding(
                            horizontal = Values.Spacing.around + Values.Spacing.normal,
                            vertical = Values.Spacing.around,
                        )
                    ) {
                        movementMethod = linkMovementMethod
                    }

                    courseDetail.onlineInfo?.let { onlineInfo ->
                        Row {
                            NormalText(
                                text = stringResource(R.string.title_online_info),
                                modifier = Modifier
                                    .padding(Values.Spacing.around)
                                    .weight(1f),
                            )

                            NormalText(
                                text = TIME_FORMAT.format(courseDetail.onlineInfoUpdatedAt!!),
                                modifier = Modifier.padding(Values.Spacing.around),
                            )
                        }

                        val onlineInfoText = onlineInfo.annotatedString()
                        val uriHandler = LocalUriHandler.current
                        ClickableText(
                            text = onlineInfoText,
                            modifier = Modifier.padding(
                                horizontal = Values.Spacing.around + Values.Spacing.normal,
                                vertical = Values.Spacing.around,
                            )
                        ) {
                            onlineInfoText.getStringAnnotations("URL", it, it)
                                .firstOrNull()?.let { url ->
                                    uriHandler.openUri(url.item)
                                }
                        }
                    }
                }
            }

            if (courseDetail.notifies.isNotEmpty()) {
                ExpandableCard(
                    title = stringResource(id = R.string.title_notifies),
                    modifier = Modifier.padding(Values.Spacing.around),
                ) {
                    Column {
                        courseDetail.notifies.forEach { notify ->
                            Notify(
                                notify = notify,
                                modifier = Modifier
                                    .padding(Values.Spacing.around)
                                    .fillMaxWidth()
                                    .clickable { openNotify(notify.id) },
                            )
                        }
                    }
                }
            }

            if (courseDetail.courseContents.isNotEmpty()) {
                ExpandableCard(
                    title = stringResource(id = R.string.title_course_contents),
                    modifier = Modifier.padding(Values.Spacing.around),
                    defaultExpanded = focusCourseContentResourceId != null,
                ) {
                    Column {
                        courseDetail.courseContents.forEach { courseContent ->
                            CourseContent(
                                courseContent = courseContent,
                                modifier = Modifier.padding(Values.Spacing.around),
                                linkMovementMethod = linkMovementMethod,
                                onMaterialClick = { material ->
                                    when (material.type) {
                                        Material.MaterialType.FILE -> {
                                            openFile(material)
                                        }
                                        Material.MaterialType.LINK -> {
                                            material.url?.let { url ->
                                                openLink(url)
                                            }
                                        }
                                        Material.MaterialType.VIDEO -> {
                                            openVideo()
                                        }
                                    }
                                },
                                focusCourseContentResourceId = focusCourseContentResourceId,
                            )
                        }
                    }
                }
            }

            if (courseDetail.reports.isNotEmpty()) {
                ExpandableCard(
                    title = stringResource(id = R.string.title_reports),
                    modifier = Modifier.padding(Values.Spacing.around),
                ) {
                    Column {
                        courseDetail.reports.forEach { report ->
                            Report(
                                report = report,
                                modifier = Modifier
                                    .padding(Values.Spacing.around)
                                    .clickable {
                                        context.startActivity(
                                            ReportDetailActivity.intent(context,
                                                courseDetail.id,
                                                report.id)
                                        )
                                    },
                            )
                        }
                    }
                }
            }

            if (courseDetail.messages.isNotEmpty()) {
                ExpandableCard(
                    title = stringResource(id = R.string.title_messages),
                    modifier = Modifier.padding(Values.Spacing.around),
                ) {
                    Column {
                        courseDetail.messages.forEach { message ->
                            Message(
                                message = message,
                                modifier = Modifier.padding(Values.Spacing.around),
                            )
                        }
                    }
                }
            }

            if (courseDetail.attendances.isNotEmpty()) {
                ExpandableCard(
                    title = stringResource(id = R.string.title_attendances),
                    modifier = Modifier.padding(Values.Spacing.around),
                ) {
                    Column {
                        courseDetail.attendances.forEach { attendance ->
                            Attendance(
                                attendance = attendance,
                                modifier = Modifier.padding(Values.Spacing.around),
                            )
                        }
                    }
                }
            }

            if (courseDetail.tests.isNotEmpty()) {
                ExpandableCard(
                    title = stringResource(id = R.string.title_tests),
                    modifier = Modifier.padding(Values.Spacing.around),
                ) {
                    Column {
                        courseDetail.tests.forEach { test ->
                            Test(
                                test = test,
                                courseId = courseDetail.id,
                                modifier = Modifier.padding(Values.Spacing.around),
                            )
                        }
                    }
                }
            }

            if (courseDetail.forums.isNotEmpty()) {
                ExpandableCard(
                    title = stringResource(id = R.string.title_forums),
                    modifier = Modifier.padding(Values.Spacing.around),
                ) {
                    Column {
                        courseDetail.forums.forEach { forum ->
                            Forum(
                                forum = forum,
                                modifier = Modifier
                                    .padding(Values.Spacing.around)
                                    .fillMaxWidth(),
                            )
                        }
                    }
                }
            }

            if (courseDetail.surveys.isNotEmpty()) {
                ExpandableCard(
                    title = stringResource(id = R.string.title_surveys),
                    modifier = Modifier.padding(Values.Spacing.around),
                ) {
                    Column {
                        courseDetail.surveys.forEach { survey ->
                            Survey(
                                survey = survey,
                                modifier = Modifier
                                    .padding(Values.Spacing.around)
                                    .fillMaxWidth(),
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        viewModel.attendanceSend.withSnackProgressBar(
            viewLifecycleOwner,
            attendanceSendSnackProgressBar,
            snackProgressBarManager,
            { viewModel.closeSendAttendance() }
        ) {
            if (it.success) {
                sendAttendanceSuccessDialog.show()
            }
        }

        viewModel.url?.let {
            customTabsIntent.launchUrl(requireContext(), Uri.parse(it))
        }
    }

    private fun openNotify(notifyId: String) {
        if (!viewModel.loadNotify(notifyId)) {
            Toast.makeText(requireContext(), R.string.toast_already_fetching, Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun openFile(material: Material) {
        downloadable = Downloadable.materialFile(viewModel.courseId, material)
        downloadable!!.open(this)
    }

    private fun openLink(url: String) {
        openingLinkDialog.setMessage(getString(R.string.dialog_message_open_link, url))
            .setPositiveButton(R.string.button_dialog_open) { _, _ ->
                customTabsIntent.launchUrl(requireContext(), url.toUri())
            }
            .show()
    }

    private fun openVideo() {
        openingVideoDialog.show()
    }

    companion object {
        @JvmStatic
        fun newInstance(): CourseDetailFragment {
            return CourseDetailFragment()
        }
    }
}
