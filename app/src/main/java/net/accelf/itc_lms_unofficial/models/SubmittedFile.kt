package net.accelf.itc_lms_unofficial.models

import java.io.Serializable
import java.util.*

data class SubmittedFile(
    val id: String,
    val comment: String,
    val submittedAt: Date,
    val file: File,
) : Serializable {

    companion object {
        val sample = SubmittedFile(
            id = "999999",
            comment = "give me scores!",
            file = File.sample,
            submittedAt = Date(),
        )
    }
}
