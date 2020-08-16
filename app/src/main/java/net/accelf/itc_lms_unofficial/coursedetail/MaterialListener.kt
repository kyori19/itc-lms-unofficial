package net.accelf.itc_lms_unofficial.coursedetail

import java.util.*

interface MaterialListener {

    fun openFile(
        materialId: String,
        resourceId: String,
        fileName: String,
        objectName: String,
        endDate: Date
    )

    fun openLink(url: String)
    fun openVideo()
}
