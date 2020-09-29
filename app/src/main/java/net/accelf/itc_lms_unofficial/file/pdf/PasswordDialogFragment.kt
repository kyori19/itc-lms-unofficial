package net.accelf.itc_lms_unofficial.file.pdf

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import kotlinx.android.synthetic.main.dialog_password.view.*
import net.accelf.itc_lms_unofficial.R

@SuppressLint("InflateParams")
class PasswordDialogFragment : DialogFragment() {

    private val layout by lazy {
        layoutInflater.inflate(R.layout.dialog_password, null)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            AlertDialog.Builder(it).apply {
                setTitle(R.string.dialog_title_pdf_require_password)
                setView(layout)

                setPositiveButton(R.string.button_dialog_open) { _, _ -> }
                setNegativeButton(R.string.button_dialog_cancel) { _, _ ->
                    setFragmentResult(
                        this@PasswordDialogFragment::class.java.simpleName,
                        bundleOf(BUNDLE_RESULT_CODE to RESULT_CANCEL)
                    )
                }
            }.create().apply {
                setOnShowListener { dialog ->
                    (dialog as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE)
                        .setOnClickListener {
                            setFragmentResult(
                                this@PasswordDialogFragment::class.java.simpleName,
                                bundleOf(
                                    BUNDLE_RESULT_CODE to RESULT_SUCCESS,
                                    BUNDLE_PASSWORD to layout.editTextDialogPassword.text.toString(),
                                )
                            )
                        }
                }
            }
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun display(fragmentManager: FragmentManager) {
        fragmentManager.beginTransaction().apply {
            if (fragmentManager.findFragmentByTag(DIALOG_TAG) is PasswordDialogFragment) {
                show(this@PasswordDialogFragment)
                layout.editTextDialogPassword.error = getString(R.string.input_error_password_wrong)
            } else {
                add(this@PasswordDialogFragment, DIALOG_TAG)
            }
            commit()
        }
    }

    fun hide(fragmentManager: FragmentManager) {
        fragmentManager.beginTransaction().apply {
            hide(this@PasswordDialogFragment)
            commit()
        }
    }

    fun dismissDialog() {
        dismiss()
    }

    companion object {
        private const val DIALOG_TAG = "pdf_password"
        const val BUNDLE_RESULT_CODE = "result_code"
        const val BUNDLE_PASSWORD = "password"
        const val RESULT_SUCCESS = 0
        const val RESULT_CANCEL = 1

        fun newInstance(): PasswordDialogFragment {
            return PasswordDialogFragment()
        }
    }
}
