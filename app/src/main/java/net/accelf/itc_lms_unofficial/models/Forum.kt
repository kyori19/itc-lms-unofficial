package net.accelf.itc_lms_unofficial.models

import net.accelf.itc_lms_unofficial.network.lmsHostUrl
import java.io.Serializable
import java.util.*

data class Forum(
    val id: String,
    val title: String,
    val from: Date,
    val until: Date,
) : Serializable {

    companion object {
        val sample = Forum(
            id = "91827",
            title = "Forum top",
            from = Date(),
            until = Date(),
        )

        fun String.getForumId(): String {
            return lmsHostUrl.newBuilder(this)!!
                .build()
                .queryParameter("forumId")!!
        }
    }
}
