package net.accelf.itc_lms_unofficial.models

import java.io.Serializable
import java.util.*

data class Material(
    val resourceId: String,
    val materialId: String,
    val type: MaterialType,
    val name: String,
    val file: File?,
    val url: String?,
    val createdAt: Date?,
    val until: Date?,
) : Serializable {

    enum class MaterialType {
        FILE,
        LINK,
        VIDEO
    }

    companion object {
        val sampleFile = Material(
            resourceId = "345678",
            materialId = "112233",
            type = MaterialType.FILE,
            name = "Distributed file",
            file = File.sample,
            url = null,
            createdAt = Date(),
            until = Date(),
        )

        val sampleLink = Material(
            resourceId = "234567",
            materialId = "878787",
            type = MaterialType.LINK,
            name = "Link to the external site",
            file = null,
            url = "https://example.com",
            createdAt = Date(),
            until = Date(),
        )
    }
}
