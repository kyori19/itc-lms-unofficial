package net.accelf.itc_lms_unofficial.reportdetail

import net.accelf.itc_lms_unofficial.models.File

interface SubmittedFileListener {

    fun openFile(file: File)
}
