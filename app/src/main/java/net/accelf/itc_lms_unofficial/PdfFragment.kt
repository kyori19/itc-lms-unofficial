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
import io.reactivex.Single
import kotlinx.android.synthetic.main.fragment_pdf.*
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.util.readWithProgress
import net.accelf.itc_lms_unofficial.util.withResponse
import okhttp3.ResponseBody
import javax.inject.Inject

@AndroidEntryPoint
class PdfFragment : Fragment(R.layout.fragment_pdf), PasswordDialogFragment.PasswordDialogListener {

    private lateinit var download: Single<ResponseBody>

    private lateinit var pdfFile: ByteArray

    @Inject
    lateinit var lms: LMS

    private val passwordDialog by lazy {
        PasswordDialogFragment.newInstance(this@PdfFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            val fileId = it.getString(ARG_FILE_ID)
            val materialId = it.getString(ARG_MATERIAL_ID)
            val endDate = it.getString(ARG_END_DATE)
            val objectName = it.getString(ARG_OBJECT_NAME)

            if ((fileId == null) != (materialId == null) || (fileId == null) != (endDate == null)
                || (fileId == null && objectName == null)
            ) {
                startActivity(MainActivity.intent(requireContext()))
                activity?.finish()
                return
            }

            download = if (fileId != null) {
                lms.downloadFile(fileId, materialId!!, endDate!!)
            } else {
                lms.downloadReportFile(objectName!!)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDownload.progressMax = 1f

        download
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
        private const val ARG_FILE_ID = "file_id"
        private const val ARG_MATERIAL_ID = "material_id"
        private const val ARG_END_DATE = "end_date"
        private const val ARG_OBJECT_NAME = "object_name"

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

        @JvmStatic
        fun newInstance(objectName: String?): PdfFragment {
            return PdfFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_OBJECT_NAME, objectName)
                }
            }
        }
    }
}
