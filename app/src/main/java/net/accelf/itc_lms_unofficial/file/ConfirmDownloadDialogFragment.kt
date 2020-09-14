package net.accelf.itc_lms_unofficial.file

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import kotlinx.android.synthetic.main.dialog_confirm_download.view.*
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.util.defaultSharedPreference

@SuppressLint("InflateParams")
class ConfirmDownloadDialogFragment : DialogFragment() {

    private val layout by lazy {
        layoutInflater.inflate(R.layout.dialog_confirm_download, null)
    }

    private lateinit var preferences: SharedPreferences

    private var uri: Uri? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            AlertDialog.Builder(activity).apply {
                setTitle(R.string.dialog_title_confirm_download)
                setView(layout)

                setPositiveButton(R.string.button_dialog_download) { _, _ ->
                    preferences.edit()
                        .putString(PREF_LAST_DOWNLOADED_URI, uri.toString())
                        .apply()

                    setFragmentResult(
                        this@ConfirmDownloadDialogFragment::class.java.simpleName,
                        bundleOf(
                            BUNDLE_RESULT_CODE to RESULT_SUCCESS,
                            BUNDLE_RESULT_TARGET_DIR to uri.toString(),
                            BUNDLE_RESULT_FILE_NAME to layout.editTextFileName.text.toString(),
                        )
                    )
                }
                setNegativeButton(R.string.button_dialog_cancel) { _, _ ->
                    setFragmentResult(
                        this@ConfirmDownloadDialogFragment::class.java.simpleName,
                        bundleOf(BUNDLE_RESULT_CODE to RESULT_CANCEL)
                    )
                }
            }.create().apply {
                setOnShowListener { dialog ->
                    val positiveButton =
                        (dialog as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE)
                    positiveButton.isEnabled = false

                    preferences = activity.defaultSharedPreference
                    preferences.getString(PREF_LAST_DOWNLOADED_URI, null)?.let {
                        updateDirectory(Uri.parse(it), positiveButton)
                    }

                    layout.textTargetDir.setOnClickListener { _ ->
                        activity.registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) {
                            it?.let {
                                updateDirectory(it, positiveButton)
                            }
                        }.launch(uri ?: Uri.EMPTY)
                    }

                    arguments?.let {
                        layout.editTextFileName.setText(it.getString(ARG_FILE_NAME))
                    }
                }
            }
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun updateDirectory(uri: Uri, positiveButton: Button) {
        this.uri = uri
        layout.textTargetDir.setText(uri.path)
        positiveButton.isEnabled = true
    }

    companion object {
        private const val ARG_FILE_NAME = "file_name"
        const val BUNDLE_RESULT_CODE = "result_code"
        const val BUNDLE_RESULT_FILE_NAME = "file_name"
        const val BUNDLE_RESULT_TARGET_DIR = "target_dir"
        private const val PREF_LAST_DOWNLOADED_URI = "last_downloaded_uri"
        const val RESULT_SUCCESS = 0
        const val RESULT_CANCEL = 1

        fun newInstance(fileName: String): ConfirmDownloadDialogFragment {
            return ConfirmDownloadDialogFragment().apply {
                arguments = bundleOf(
                    ARG_FILE_NAME to fileName
                )
            }
        }
    }
}
