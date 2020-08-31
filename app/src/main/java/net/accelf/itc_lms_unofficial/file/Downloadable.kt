package net.accelf.itc_lms_unofficial.file

import android.content.Context
import io.reactivex.Single
import net.accelf.itc_lms_unofficial.models.File
import net.accelf.itc_lms_unofficial.models.Material
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.util.TIME_SECONDS_FORMAT
import okhttp3.ResponseBody
import java.io.Serializable
import java.util.*

data class Downloadable(
    val type: Type,
    val file: File,
    val materialParams: MaterialParams?,
) : Serializable {

    fun download(lms: LMS): Single<ResponseBody> {
        return when (type) {
            Type.MATERIAL -> lms.getFileId(materialParams!!.courseId, materialParams.materialId,
                materialParams.resourceId, file.fileName, file.objectName)
                .flatMap {
                    lms.downloadMaterialFile(it,
                        materialParams.materialId,
                        TIME_SECONDS_FORMAT.format(materialParams.endDate))
                }
            Type.REPORT -> lms.downloadReportFile(file.objectName)
        }
    }

    fun open(context: Context) {
        if (file.fileName.endsWith(".pdf")) {
            context.startActivity(PdfActivity.intent(context, this))
        }
    }

    companion object {

        fun materialFile(courseId: String, material: Material): Downloadable {
            return Downloadable(
                Type.MATERIAL,
                material.file!!,
                MaterialParams(
                    courseId,
                    material.materialId,
                    material.resourceId,
                    material.until!!,
                ),
            )
        }

        fun reportFile(file: File): Downloadable {
            return Downloadable(
                Type.REPORT,
                file,
                null,
            )
        }
    }

    enum class Type {
        MATERIAL,
        REPORT,
    }

    data class MaterialParams(
        val courseId: String,
        val materialId: String,
        val resourceId: String,
        val endDate: Date,
    ) : Serializable
}
