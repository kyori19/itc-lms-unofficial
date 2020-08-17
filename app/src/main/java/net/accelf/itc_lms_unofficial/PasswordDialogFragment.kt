package net.accelf.itc_lms_unofficial

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.dialog_password.view.*

@SuppressLint("InflateParams")
class PasswordDialogFragment : DialogFragment() {

    private val layout by lazy {
        layoutInflater.inflate(R.layout.dialog_password, null)
    }

    private val listener by lazy {
        targetFragment as PasswordDialogListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            AlertDialog.Builder(it).apply {
                setTitle(R.string.dialog_title_pdf_require_password)
                setView(layout)

                setPositiveButton(R.string.button_dialog_open) { _, _ -> }
                setNegativeButton(R.string.button_dialog_cancel) { _, _ ->
                    listener.onPasswordCancel()
                }
            }.create().apply {
                setOnShowListener { dialog ->
                    (dialog as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE)
                        .setOnClickListener {
                            listener.onPasswordSubmit(
                                dialog,
                                layout.editTextDialogPassword.text.toString()
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

    interface PasswordDialogListener {
        fun onPasswordSubmit(dialog: DialogInterface, password: String)
        fun onPasswordCancel()
    }

    companion object {
        private const val DIALOG_TAG = "pdf_password"

        fun <T> newInstance(listener: T): PasswordDialogFragment where T : Fragment, T : PasswordDialogListener {
            return PasswordDialogFragment().apply {
                setTargetFragment(listener, 0)
            }
        }
    }
}
