package net.accelf.itc_lms_unofficial.file.download

import android.net.Uri
import androidx.lifecycle.ViewModel
import net.accelf.itc_lms_unofficial.util.mutableLiveDataOf

class DownloadDialogViewModel : ViewModel() {

    lateinit var mimeType: String
    lateinit var defaultFileName: String

    val targetDirectoryUri = mutableLiveDataOf<Uri?>(null)
    val fileName = mutableLiveDataOf("")

    val targetDocumentUri = mutableLiveDataOf<Uri?>(null)

    fun init(defaultDir: Uri?, mimeType: String, defaultFileName: String) {
        this.mimeType = mimeType
        this.defaultFileName = defaultFileName
        targetDirectoryUri.postValue(defaultDir)
        fileName.postValue(defaultFileName)
        targetDocumentUri.postValue(null)
    }

    fun filled(tabIndex: Int): Boolean {
        return when (tabIndex) {
            0 -> targetDirectoryUri.value != null && fileName.value!!.isNotEmpty()
            1 -> targetDocumentUri.value != null
            else -> throw IllegalArgumentException()
        }
    }
}
