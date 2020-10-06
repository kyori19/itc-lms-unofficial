package net.accelf.itc_lms_unofficial.file.pdf

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import net.accelf.itc_lms_unofficial.file.download.Downloadable
import net.accelf.itc_lms_unofficial.file.pdf.PdfActivity.Companion.EXTRA_DOWNLOADABLE
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.util.*

class PdfViewModel @ViewModelInject constructor(
    private val lms: LMS,
    @Assisted private val savedState: SavedStateHandle,
) : RxAwareViewModel() {

    val downloadable = savedState.get<Downloadable>(EXTRA_DOWNLOADABLE)!!
    var openingPage: Int
        get() = savedState.get<Int>(STATE_OPEN_PAGE) ?: 0
        set(value) {
            savedState.set(STATE_OPEN_PAGE, value)
        }

    private val mutablePdfFile = mutableRequestOf<ByteArray>()
    val pdfFile: LiveData<Request<ByteArray>> = mutablePdfFile

    init {
        load()
    }

    private fun load() {
        downloadable.download(lms)
            .map {
                val fullLength = it.contentLength()
                it.byteStream().readWithProgress { readBytes ->
                    mutablePdfFile.postValue(Progress(readBytes.toFloat() / fullLength))
                }
            }.toLiveData(mutablePdfFile)
    }

    companion object {
        private const val STATE_OPEN_PAGE = "open_page"
    }
}
