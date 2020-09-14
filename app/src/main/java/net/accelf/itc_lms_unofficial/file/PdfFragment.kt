package net.accelf.itc_lms_unofficial.file

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.github.polesapart.pdfviewer.PDFView
import com.shockwave.pdfium.PdfPasswordException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_pdf.*
import net.accelf.itc_lms_unofficial.CHANNEL_ID_DOWNLOADS
import net.accelf.itc_lms_unofficial.NOTIFICATION_ID_DOWNLOAD_PROGRESS
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.file.PasswordDialogFragment.Companion.BUNDLE_PASSWORD
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.util.readWithProgress
import net.accelf.itc_lms_unofficial.util.withResponse
import net.accelf.itc_lms_unofficial.util.writeToFile
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@AndroidEntryPoint
class PdfFragment : Fragment(R.layout.fragment_pdf) {

    private lateinit var downloadable: Downloadable

    private lateinit var pdfFile: ByteArray

    @Inject
    lateinit var lms: LMS

    @Inject
    lateinit var notificationId: AtomicInteger

    private val passwordDialog by lazy {
        PasswordDialogFragment.newInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        arguments?.let {
            downloadable = it.getSerializable(ARG_DOWNLOADABLE) as Downloadable
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener(PasswordDialogFragment::class.java.simpleName) { _, bundle ->
            when (bundle.getInt(PasswordDialogFragment.BUNDLE_RESULT_CODE)) {
                PasswordDialogFragment.RESULT_SUCCESS -> {
                    passwordDialog.hide(parentFragmentManager)

                    pdfView.fromBytes(pdfFile)
                        .setDefaults()
                        .password(bundle.getString(BUNDLE_PASSWORD))
                        .onLoad { _, _, _ ->
                            passwordDialog.dismissDialog()
                        }
                        .load()
                }
                PasswordDialogFragment.RESULT_CANCEL -> {
                    activity?.finish()
                }
            }
        }

        progressDownload.progressMax = 1f

        downloadable.download(lms)
            .map {
                val fullLength = it.contentLength()
                it.byteStream().readWithProgress { readBytes ->
                    progressDownload.progress = readBytes.toFloat() / fullLength
                }
            }.withResponse(activity as AppCompatActivity) {
                pdfFile = it

                pdfView.apply {
                    visibility = VISIBLE
                    fromBytes(pdfFile)
                        .setDefaults()
                        .load()
                }

                activity?.invalidateOptionsMenu()
            }
    }

    private fun PDFView.Configurator.setDefaults(): PDFView.Configurator {
        return spacing(1)
            .enableAnnotationRendering(true)
            .onError {
                if (it is PdfPasswordException) {
                    passwordDialog.display(parentFragmentManager)
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        menu.findItem(R.id.actionDownload)?.isVisible = ::pdfFile.isInitialized
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionDownload -> {
                val dialog = ConfirmDownloadDialogFragment.newInstance(downloadable.file.fileName)
                setFragmentResultListener(ConfirmDownloadDialogFragment::class.java.simpleName) { _, it ->
                    when (it.getInt(ConfirmDownloadDialogFragment.BUNDLE_RESULT_CODE)) {
                        ConfirmDownloadDialogFragment.RESULT_SUCCESS -> {
                            val file = requireContext().writeToFile(
                                Uri.parse(it.getString(ConfirmDownloadDialogFragment.BUNDLE_RESULT_TARGET_DIR)),
                                it.getString(ConfirmDownloadDialogFragment.BUNDLE_RESULT_FILE_NAME,
                                    ""),
                                MIME_PDF,
                                pdfFile
                            )

                            val id =
                                NOTIFICATION_ID_DOWNLOAD_PROGRESS + notificationId.incrementAndGet()
                            val notification =
                                NotificationCompat.Builder(requireContext(), CHANNEL_ID_DOWNLOADS)
                                    .apply {
                                        setSmallIcon(R.drawable.ic_download)
                                        setContentTitle(downloadable.file.fileName)
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
                                            PendingIntent.getActivity(context, id, chooser, 0)
                                        setContentIntent(pendingIntent)
                                    }.build()
                            NotificationManagerCompat.from(requireContext())
                                .notify(id, notification)
                        }
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
        private const val ARG_DOWNLOADABLE = "downloadable"
        private const val MIME_PDF = "application/pdf"

        @JvmStatic
        fun newInstance(downloadable: Downloadable): PdfFragment {
            return PdfFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_DOWNLOADABLE, downloadable)
                }
            }
        }
    }
}
