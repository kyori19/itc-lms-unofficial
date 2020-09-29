package net.accelf.itc_lms_unofficial.file.download

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.google.gson.Gson
import net.accelf.itc_lms_unofficial.util.fromJson
import java.io.Serializable

sealed class DownloadDialogResult : Serializable {

    abstract fun file(context: Context, mimeType: String): DocumentFile

    fun writeToFile(context: Context, mimeType: String, content: ByteArray): DocumentFile {
        val file = file(context, mimeType)
        val os = context.contentResolver.openOutputStream(file.uri)!!
        os.write(content)
        os.close()
        return file
    }

    companion object {

        @Suppress("SENSELESS_COMPARISON")
        fun Gson.fromJsonToResult(json: String?): DownloadDialogResult {
            return fromJson<TreeTypeResult>(json)
                .takeUnless { it.dirUri == null }
                ?: fromJson<DocTypeResult>(json)
        }
    }
}

data class TreeTypeResult(
    val dirUri: Uri,
    val fileName: String,
) : DownloadDialogResult() {

    override fun file(context: Context, mimeType: String): DocumentFile {
        val dir = DocumentFile.fromTreeUri(context, dirUri)!!
        return dir.createFile(mimeType, fileName)!!
    }
}

data class DocTypeResult(
    val docUri: Uri,
) : DownloadDialogResult() {

    override fun file(context: Context, mimeType: String): DocumentFile {
        return DocumentFile.fromSingleUri(context, docUri)!!
    }
}
