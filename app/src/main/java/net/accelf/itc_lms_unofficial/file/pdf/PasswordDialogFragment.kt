package net.accelf.itc_lms_unofficial.file.pdf

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.ui.PasswordField
import net.accelf.itc_lms_unofficial.ui.Values
import net.accelf.itc_lms_unofficial.ui.compose

@SuppressLint("InflateParams")
class PasswordDialogFragment : DialogFragment() {

    private val mutablePassword = mutableStateOf("")
    private val mutableIsWrong = mutableStateOf(false)

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            AlertDialog.Builder(it).apply {
                setTitle(R.string.dialog_title_pdf_require_password)
                setView(compose {
                    PasswordDialogFragmentContent()
                })

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
                                    BUNDLE_PASSWORD to mutablePassword.value,
                                )
                            )
                        }
                }
            }
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    @ExperimentalComposeUiApi
    @Composable
    @Preview
    private fun PasswordDialogFragmentContent() {
        val isWrong by remember { mutableIsWrong }

        Column(
            modifier = Modifier.padding(Values.Spacing.around),
        ) {
            PasswordField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Values.Spacing.around),
                mutableValue = mutablePassword,
                label = { Text(text = stringResource(id = R.string.input_hint_password)) },
                isError = isWrong,
            )

            if (isWrong) {
                Text(
                    modifier = Modifier.padding(Values.Spacing.around),
                    text = stringResource(id = R.string.input_error_password_wrong),
                    color = MaterialTheme.colors.error,
                    fontSize = Values.Text.small,
                )
            }
        }
    }

    fun display(fragmentManager: FragmentManager) {
        fragmentManager.beginTransaction().apply {
            if (fragmentManager.findFragmentByTag(DIALOG_TAG) is PasswordDialogFragment) {
                show(this@PasswordDialogFragment)
                mutableIsWrong.value = true
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
