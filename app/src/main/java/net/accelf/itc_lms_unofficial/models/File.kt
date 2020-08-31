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
        FAILED("9");

        companion object {
            fun fromText(text: String): ScanStatus {
                return values().first { it.text == text }
            }
        }
    }
}