package net.accelf.itc_lms_unofficial.coursedetail

import net.accelf.itc_lms_unofficial.models.Material

interface MaterialListener {

    fun openFile(material: Material)
    fun openLink(url: String)
    fun openVideo()
}
