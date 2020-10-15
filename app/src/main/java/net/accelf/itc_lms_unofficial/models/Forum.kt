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

        fun String.getForumId(): String {
            return lmsHostUrl.newBuilder(this)!!
                .build()
                .queryParameter("forumId")!!
        }
    }
}
