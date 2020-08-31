package net.accelf.itc_lms_unofficial.file

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.github.polesapart.pdfviewer.PDFView
import com.shockwave.pdfium.PdfPasswordException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_pdf.*
import net.accelf.itc_lms_unofficial.PasswordDialogFragment
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.util.readWithProgress
import net.accelf.itc_lms_unofficial.util.withResponse
import javax.inject.Inject

@AndroidEntryPoint
class PdfFragment : Fragment(R.layout.fragment_pdf), PasswordDialogFragment.PasswordDialogListener {

    private lateinit var downloadable: Downloadable

    private lateinit var pdfFile: ByteArray

    @Inject
    lateinit var lms: LMS

    private val passwordDialog by lazy {
        PasswordDialogFragment.newInstance(this@PdfFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            downloadable = it.getSerializable(ARG_DOWNLOADABLE) as Downloadable
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

    override fun onPasswordSubmit(dialog: DialogInterface, password: String) {
        passwordDialog.hide(parentFragmentManager)

        pdfView.fromBytes(pdfFile)
            .setDefaults()
            .password(password)
            .onLoad { _, _, _ ->
                dialog.dismiss()
            }
            .load()
    }

    override fun onPasswordCancel() {
        activity?.finish()
    }

    companion object {
        private const val ARG_DOWNLOADABLE = "downloadable"

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
