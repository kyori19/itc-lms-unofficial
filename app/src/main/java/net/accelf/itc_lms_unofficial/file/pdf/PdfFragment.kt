package net.accelf.itc_lms_unofficial.file.pdf

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import com.github.polesapart.pdfviewer.PDFView
import com.shockwave.pdfium.PdfPasswordException
import dagger.hilt.android.AndroidEntryPoint
import net.accelf.itc_lms_unofficial.Notifications
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.file.download.ConfirmDownloadDialogFragment
import net.accelf.itc_lms_unofficial.file.download.DownloadDialogResult
import net.accelf.itc_lms_unofficial.file.pdf.PasswordDialogFragment.Companion.BUNDLE_PASSWORD
import net.accelf.itc_lms_unofficial.ui.compose
import net.accelf.itc_lms_unofficial.util.*
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@AndroidEntryPoint
class PdfFragment : Fragment() {

    private val passwordDialog by lazy {
        PasswordDialogFragment.newInstance()
    }

    @Inject
    lateinit var notificationId: AtomicInteger

    private val viewModel by activityViewModels<PdfViewModel>()
    private val mutablePassword = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return compose {
            PdfFragmentContent()
        }
    }

    @Composable
    @Preview
    private fun PdfFragmentContent() {
        val pdfFile by viewModel.pdfFile.observeAsState()
        val password by remember { mutablePassword }

        val background = MaterialTheme.colors.background.value.toInt()

        AndroidView(
            { PDFView(it, null) },
        ) {
            if (pdfFile is Success) {
                it.setBackgroundColor(background)
                it.fromBytes((pdfFile as Success).data)
                    .spacing(1)
                    .enableAnnotationRendering(true)
                    .defaultPage(viewModel.openingPage)
                    .onLoad { _, _, _ ->
                        viewModel.pdfTitle.postValue(it.documentMeta.title)
                    }
                    .onPageChange { page, _ ->
                        viewModel.openingPage = page
                    }
                    .onError { e ->
                        if (e is PdfPasswordException) {
                            passwordDialog.display(parentFragmentManager)
                        }
                    }
                    .let { config ->
                        if (password.isEmpty()) {
                            return@let config
                        }

                        return@let config.password(password)
                            .onLoad { _, _, _ ->
                                passwordDialog.dismissDialog()
                            }
                    }
                    .load()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setFragmentResultListener(PasswordDialogFragment::class.java.simpleName) { _, bundle ->
            when (bundle.getInt(PasswordDialogFragment.BUNDLE_RESULT_CODE)) {
                PasswordDialogFragment.RESULT_SUCCESS -> {
                    passwordDialog.hide(parentFragmentManager)

                    mutablePassword.value = bundle.getString(BUNDLE_PASSWORD) ?: ""
                }
                PasswordDialogFragment.RESULT_CANCEL -> {
                    activity?.finish()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        menu.findItem(R.id.actionDownload)?.isVisible = viewModel.pdfFile.value is Success
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionDownload -> {
                val dialog =
                    ConfirmDownloadDialogFragment.newInstance(viewModel.downloadable.file.fileName)
                setFragmentResultListener(ConfirmDownloadDialogFragment::class.java.simpleName) { _, it ->
                    @Suppress("UNCHECKED_CAST")
                    (it.getSerializable(ConfirmDownloadDialogFragment.BUNDLE_RESULT) as Result<DownloadDialogResult>).onSuccess {
                        val file = it.writeToFile(requireContext(),
                            MIME_PDF,
                            (viewModel.pdfFile.value as Success).data)

                        val id =
                            Notifications.Ids.DOWNLOAD_PROGRESS + notificationId.incrementAndGet()
                        val notification =
                            NotificationCompat.Builder(requireContext(),
                                Notifications.Channels.DOWNLOADS)
                                .apply {
                                    setSmallIcon(R.drawable.ic_download)
                                    setContentTitle(viewModel.downloadable.file.fileName)
                                    setContentText(getString(R.string.notify_text_downloaded))

                                    priority = NotificationCompat.PRIORITY_LOW
                                    setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                                    setAutoCancel(true)

                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        setDataAndType(file.uri, file.type)
                                    }
                                    val chooser = Intent.createChooser(intent, file.name)
                                    val pendingIntent =
                                        PendingIntent.getActivity(context,
                                            id,
                                            chooser,
                                            PendingIntent.FLAG_IMMUTABLE)
                                    setContentIntent(pendingIntent)
                                }.build()
                        requireContext().notify(id, notification)
                    }
                }
                dialog.show(parentFragmentManager,
                    ConfirmDownloadDialogFragment::class.java.simpleName)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val MIME_PDF = "application/pdf"

        @JvmStatic
        fun newInstance(): PdfFragment {
            return PdfFragment()
        }
    }
}
