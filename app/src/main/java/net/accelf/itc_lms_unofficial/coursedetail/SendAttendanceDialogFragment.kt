package net.accelf.itc_lms_unofficial.coursedetail

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import kotlinx.android.synthetic.main.dialog_send_attendance.view.*
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.util.Success
import net.accelf.itc_lms_unofficial.util.isNotNullOrEmpty

@SuppressLint("InflateParams")
class SendAttendanceDialogFragment : DialogFragment() {

    private val layout by lazy {
        layoutInflater.inflate(R.layout.dialog_send_attendance, null)
    }

    private val viewModel by activityViewModels<CourseDetailViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            AlertDialog.Builder(activity).apply {
                setTitle(R.string.dialog_title_send_attendance)
                setView(layout)

                setPositiveButton(R.string.button_dialog_send) { _, _ ->
                    setFragmentResult(
                        this@SendAttendanceDialogFragment::class.java.simpleName,
                        bundleOf(
                            BUNDLE_RESULT to Result.success(SendAttendanceDialogResult(
                                layout.editTextAttendancePassword.text.toString(),
                                layout.editTextAttendanceComment.text.toString()))
                        )
                    )
                }
                setNegativeButton(R.string.button_dialog_cancel) { _, _ ->
                    setFragmentResult(
                        this@SendAttendanceDialogFragment::class.java.simpleName,
                        bundleOf(BUNDLE_RESULT to Result.failure<SendAttendanceDialogResult>(
                            Throwable()))
                    )
                }
            }.create().apply {
                setOnShowListener { dialog ->
                    val positiveButton =
                        (dialog as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE)
                    positiveButton.isEnabled = false

                    (viewModel.attendanceSend.value as Success).data.let { attendanceSend ->
                        layout.textAttendanceAlreadySent.visibility = when (attendanceSend.sent) {
                            true -> VISIBLE
                            false -> GONE
                        }

                        layout.textAttendanceError.visibility =
                            when (attendanceSend.errorOnPassword.isNotEmpty() || attendanceSend.errorOnComment.isNotEmpty()) {
                                true -> VISIBLE
                                false -> GONE
                            }

                        // Warning: Using apply causes wrong `this` suggestion
                        layout.inputLayoutAttendancePassword.error = attendanceSend.errorOnPassword
                        layout.editTextAttendancePassword.setText(attendanceSend.password)
                        layout.editTextAttendancePassword.addTextChangedListener {
                            layout.inputLayoutAttendancePassword.isErrorEnabled = false
                            positiveButton.isEnabled = it.isNotNullOrEmpty()
                        }

                        layout.inputLayoutAttendanceComment.error = attendanceSend.errorOnComment
                        layout.editTextAttendanceComment.setText(attendanceSend.comment)
                        layout.editTextAttendanceComment.addTextChangedListener {
                            layout.inputLayoutAttendanceComment.isErrorEnabled = false
                        }
                    }
                }
            }
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.closeSendAttendance()
    }

    companion object {
        const val BUNDLE_RESULT = "result"

        fun newInstance(): SendAttendanceDialogFragment {
            return SendAttendanceDialogFragment()
        }
    }
}
