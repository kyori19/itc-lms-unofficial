package net.accelf.itc_lms_unofficial.models

import java.io.Serializable
import java.util.*

data class Material(
    val resourceId: String,
    val materialId: String,
    val type: MaterialType,
    val name: String,
    val objectName: String?,
    val url: String?,
    val scanStatus: ScanStatus,
    val createdAt: Date?,
    val until: Date?
) : Serializable {

    enum class MaterialType {
        FILE,
        LINK,
        VIDEO
    }

    enum class ScanStatus {
        SCANNED,
        NOT_SCANNED,
        FAILED,
        NOT_A_FILE
    }
}
