package net.accelf.itc_lms_unofficial

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
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.util.readWithProgress
import net.accelf.itc_lms_unofficial.util.withResponse
import javax.inject.Inject

@AndroidEntryPoint
class PdfFragment : Fragment(R.layout.fragment_pdf), PasswordDialogFragment.PasswordDialogListener {

    private lateinit var fileId: String
    private lateinit var materialId: String
    private lateinit var endDate: String

    private lateinit var pdfFile: ByteArray

    @Inject
    lateinit var lms: LMS

    private val passwordDialog by lazy {
        PasswordDialogFragment.newInstance(this@PdfFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.getString(ARG_FILE_ID) == null || it.getString(ARG_MATERIAL_ID) == null
                || it.getString(ARG_END_DATE) == null
            ) {
                startActivity(MainActivity.intent(requireContext()))
                activity?.finish()
                return
            }

            fileId = it.getString(ARG_FILE_ID)!!
            materialId = it.getString(ARG_MATERIAL_ID)!!
            endDate = it.getString(ARG_END_DATE)!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDownload.progressMax = 1f

        lms.downloadFile(fileId, materialId, endDate)
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
        private const val ARG_FILE_ID = "file_id"
        private const val ARG_MATERIAL_ID = "material_id"
        private const val ARG_END_DATE = "end_date"

        @JvmStatic
        fun newInstance(fileId: String?, materialId: String?, endDate: String?): PdfFragment {
            return PdfFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_FILE_ID, fileId)
                    putString(ARG_MATERIAL_ID, materialId)
                    putString(ARG_END_DATE, endDate)
                }
            }
        }
    }
}
