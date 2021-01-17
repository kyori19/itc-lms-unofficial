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
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.databinding.DialogSendAttendanceBinding
import net.accelf.itc_lms_unofficial.util.Success
import net.accelf.itc_lms_unofficial.util.isNotNullOrEmpty

@SuppressLint("InflateParams")
class SendAttendanceDialogFragment : DialogFragment() {

    private val binding by lazy {
        DialogSendAttendanceBinding.inflate(layoutInflater)
    }

    private val viewModel by activityViewModels<CourseDetailViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            AlertDialog.Builder(activity).apply {
                setTitle(R.string.dialog_title_send_attendance)
                setView(binding.root)

                setPositiveButton(R.string.button_dialog_send) { _, _ ->
                    setFragmentResult(
                        this@SendAttendanceDialogFragment::class.java.simpleName,
                        bundleOf(
                            BUNDLE_RESULT to Result.success(SendAttendanceDialogResult(
                                binding.editTextAttendancePassword.text.toString(),
                                binding.editTextAttendanceComment.text.toString()))
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
                        binding.textAttendanceAlreadySent.visibility = when (attendanceSend.sent) {
                            true -> VISIBLE
                            false -> GONE
                        }

                        binding.textAttendanceError.visibility =
                            when (attendanceSend.errorOnPassword.isNotEmpty() || attendanceSend.errorOnComment.isNotEmpty()) {
                                true -> VISIBLE
                                false -> GONE
                            }

                        // Warning: Using apply causes wrong `this` suggestion
                        binding.inputLayoutAttendancePassword.error = attendanceSend.errorOnPassword
                        binding.editTextAttendancePassword.setText(attendanceSend.password)
                        binding.editTextAttendancePassword.addTextChangedListener {
                            binding.inputLayoutAttendancePassword.isErrorEnabled = false
                            positiveButton.isEnabled = it.isNotNullOrEmpty()
                        }

                        binding.inputLayoutAttendanceComment.error = attendanceSend.errorOnComment
                        binding.editTextAttendanceComment.setText(attendanceSend.comment)
                        binding.editTextAttendanceComment.addTextChangedListener {
                            binding.inputLayoutAttendanceComment.isErrorEnabled = false
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
