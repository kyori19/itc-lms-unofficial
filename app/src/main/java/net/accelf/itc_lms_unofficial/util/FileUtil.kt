package net.accelf.itc_lms_unofficial.util

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile

fun Context.writeToFile(
    dirUri: Uri,
    fileName: String,
    mimeType: String,
    content: ByteArray,
): DocumentFile {
    val dir = DocumentFile.fromTreeUri(this, dirUri)!!
    val file = dir.createFile(mimeType, fileName)!!

    val os = contentResolver.openOutputStream(file.uri)!!
    os.write(content)
    os.close()

    return file
}
