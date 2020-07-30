package net.accelf.itc_lms_unofficial.models

import net.accelf.itc_lms_unofficial.network.lmsHostUrl
import java.io.Serializable
import java.util.*

fun String.getSurveyId(): String {
    return lmsHostUrl.newBuilder(this)!!
        .build()
        .queryParameter("surveyId")!!
}

data class Survey(
    val id: String,
    val title: String,
    val from: Date,
    val until: Date
) : Serializable
