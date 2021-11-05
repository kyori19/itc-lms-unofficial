package net.accelf.itc_lms_unofficial.file.download

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import net.accelf.itc_lms_unofficial.Prefs
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.databinding.DialogDownloadFrameBinding
import net.accelf.itc_lms_unofficial.util.defaultSharedPreference

@SuppressLint("InflateParams")
class ConfirmDownloadDialogFragment : DialogFragment() {

    private val binding by lazy {
        DialogDownloadFrameBinding.inflate(layoutInflater)
    }

    private val viewModel by activityViewModels<DownloadDialogViewModel>()

    private val preferences by lazy {
        requireContext().defaultSharedPreference
    }

    private var tabIndex = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            AlertDialog.Builder(activity).apply {
                setTitle(R.string.dialog_title_confirm_download)
                setView(binding.root)

                setPositiveButton(R.string.button_dialog_download) { _, _ ->
                    when (tabIndex) {
                        0 -> {
                            val dirUri = viewModel.targetDirectoryUri.value!!

                            preferences.edit()
                                .putString(Prefs.Keys.LAST_DOWNLOADED_URI, dirUri.toString())
                                .apply()

                            setFragmentResult(
                                this@ConfirmDownloadDialogFragment::class.java.simpleName,
                                bundleOf(
                                    BUNDLE_RESULT to Result.success(
                                        TreeTypeResult(dirUri, viewModel.fileName.value.toString())
                                    )
                                )
                            )
                        }
                        1 -> {
                            setFragmentResult(
                                this@ConfirmDownloadDialogFragment::class.java.simpleName,
                                bundleOf(
                                    BUNDLE_RESULT to Result.success(
                                        DocTypeResult(viewModel.targetDocumentUri.value!!)
                                    )
                                )
                            )
                        }
                    }
                }
                setNegativeButton(R.string.button_dialog_cancel) { _, _ ->
                    setFragmentResult(
                        this@ConfirmDownloadDialogFragment::class.java.simpleName,
                        bundleOf(BUNDLE_RESULT to Result.failure<DownloadDialogResult>(Throwable()))
                    )
                }
            }.create().apply {
                setOnShowListener { dialog ->
                    val positiveButton =
                        (dialog as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE)
                    positiveButton.isEnabled = false

                    binding.viewPager.apply {
                        adapter = TabAdapter(this@ConfirmDownloadDialogFragment)

                        registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                            override fun onPageScrollStateChanged(state: Int) {
                                positiveButton.isEnabled = when (state) {
                                    ViewPager2.SCROLL_STATE_DRAGGING -> false
                                    ViewPager2.SCROLL_STATE_IDLE,
                                    ViewPager2.SCROLL_STATE_SETTLING,
                                    -> viewModel.filled(tabIndex)
                                    else -> throw IllegalArgumentException()
                                }
                            }

                            override fun onPageSelected(position: Int) {
                                tabIndex = position
                                positiveButton.isEnabled = viewModel.filled(position)
                            }
                        })

                        TabLayoutMediator(binding.tabLayout, this) { tab, index ->
                            tab.setText(
                                when (index) {
                                    0 -> R.string.dialog_tab_title_download_tree
                                    1 -> R.string.dialog_tab_title_download_doc
                                    else -> throw IllegalArgumentException()
                                }
                            )
                        }.attach()
                    }

                    listOf(
                        0 to listOf(viewModel.targetDirectoryUri, viewModel.fileName),
                        1 to listOf(viewModel.targetDocumentUri),
                    ).forEach { pair ->
                        pair.second.forEach {
                            it.observe(activity) {
                                if (tabIndex == pair.first) {
                                    positiveButton.isEnabled = viewModel.filled(pair.first)
                                }
                            }
                        }
                    }

                    viewModel.init(
                        preferences.getString(Prefs.Keys.LAST_DOWNLOADED_URI, null)
                            ?.let { Uri.parse(it) },
                        arguments?.getString(ARG_MIME_TYPE) ?: "*/*",
                        arguments?.getString(ARG_FILE_NAME, null) ?: "",
                    )
                }
            }
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        private const val ARG_MIME_TYPE = "mime_type"
        private const val ARG_FILE_NAME = "file_name"
        const val BUNDLE_RESULT = "result"

        fun newInstance(mimeType: String, fileName: String): ConfirmDownloadDialogFragment {
            return ConfirmDownloadDialogFragment().apply {
                arguments = bundleOf(
                    ARG_MIME_TYPE to mimeType,
                    ARG_FILE_NAME to fileName,
                )
            }
        }
    }
}
