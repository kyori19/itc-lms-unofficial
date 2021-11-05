package net.accelf.itc_lms_unofficial.file.download

import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.google.gson.Gson
import io.reactivex.rxjava3.core.Single
import net.accelf.itc_lms_unofficial.file.download.ConfirmDownloadDialogFragment.Companion.BUNDLE_RESULT
import net.accelf.itc_lms_unofficial.file.pdf.PdfActivity
import net.accelf.itc_lms_unofficial.models.File
import net.accelf.itc_lms_unofficial.models.Material
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.permission.Permission
import net.accelf.itc_lms_unofficial.permission.PermissionRequestable
import net.accelf.itc_lms_unofficial.permission.PermissionRequestable.Companion.preparePermissionRequest
import net.accelf.itc_lms_unofficial.util.TIME_SECONDS_FORMAT
import okhttp3.ResponseBody
import java.io.Serializable
import java.util.*

data class Downloadable(
    val type: Type,
    val file: File,
    val courseId: String,
    val materialParams: MaterialParams?,
) : Serializable {

    fun download(lms: LMS): Single<ResponseBody> {
        return when (type) {
            Type.MATERIAL -> lms.getFileId(courseId, materialParams!!.materialId,
                materialParams.resourceId, file.fileName, file.objectName)
                .flatMap {
                    lms.downloadMaterialFile(it,
                        courseId,
                        materialParams.materialId,
                        TIME_SECONDS_FORMAT.format(materialParams.endDate))
                }
            Type.REPORT -> lms.downloadReportFile(file.objectName, courseId)
        }
    }

    fun <T> open(fragment: T) where T : Fragment, T : PermissionRequestable, T : ProvidesGson {
        val context = fragment.requireContext()
        if (file.fileName.endsWith(".pdf")) {
            context.startActivity(PdfActivity.intent(context, this))
            return
        }

        if (!permission.granted(context)) {
            permission.request(fragment)
            return
        }

        downloadWithDialog(fragment)
    }

    private fun <T> downloadWithDialog(fragment: T) where T : Fragment, T : ProvidesGson {
        val confirmDownloadDialog = ConfirmDownloadDialogFragment.newInstance(
            MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file.fileName))
                ?: "*/*",
            file.fileName,
        )
        fragment.setFragmentResultListener(ConfirmDownloadDialogFragment::class.java.simpleName) { _, it ->
            @Suppress("UNCHECKED_CAST")
            (it.getSerializable(BUNDLE_RESULT) as Result<DownloadDialogResult>).onSuccess {
                FileDownloadWorker.enqueue(fragment.requireContext(), fragment.gson, this, it)
            }
        }
        confirmDownloadDialog.show(fragment.parentFragmentManager,
            ConfirmDownloadDialogFragment::class.java.simpleName)
    }

    companion object {

        private val permission = Permission.WRITE_EXTERNAL_STORAGE

        fun materialFile(courseId: String, material: Material): Downloadable {
            return Downloadable(
                Type.MATERIAL,
                material.file!!,
                courseId,
                MaterialParams(
                    material.materialId,
                    material.resourceId,
                    material.until!!,
                ),
            )
        }

        fun reportFile(courseId: String, file: File): Downloadable {
            return Downloadable(
                Type.REPORT,
                file,
                courseId,
                null,
            )
        }

        fun <T> T.preparePermissionRequestForDownloadable(getDownloadable: () -> Downloadable): ActivityResultLauncher<String>
                where T : Fragment, T : ProvidesGson {
            return preparePermissionRequest({ permission }) {
                val downloadable = getDownloadable()
                downloadable.downloadWithDialog(this)
            }
        }
    }

    enum class Type {
        MATERIAL,
        REPORT,
    }

    data class MaterialParams(
        val materialId: String,
        val resourceId: String,
        val endDate: Date,
    ) : Serializable

    interface ProvidesGson {
        var gson: Gson
    }
}
