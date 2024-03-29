package net.accelf.itc_lms_unofficial.models

import java.io.Serializable

data class File(
    val objectName: String,
    val fileName: String,
    val scanStatus: ScanStatus,
) : Serializable {

    enum class ScanStatus(val text: String) {
        SCANNED("1"),
        NOT_SCANNED("0"),
        FAILED("9"),
        UNKNOWN(""),
        ;

        companion object {
            fun String?.toScanStatus(): ScanStatus =
                values().firstOrNull { it.text == this } ?: UNKNOWN
        }
    }

    companion object {
        val sample = File(
            objectName = "2020/ab/cd/ef/12345678-90ab-cdef-1234-567890abcdef",
            fileName = "Attachment file 1",
            scanStatus = ScanStatus.SCANNED,
        )
    }
}
