package net.accelf.itc_lms_unofficial.models

import net.accelf.itc_lms_unofficial.network.lmsHostUrl
import java.io.Serializable
import java.util.*

data class Survey(
    val id: String,
    val title: String,
    val from: Date,
    val until: Date,
) : Serializable {

    companion object {
        val sample = Survey(
            id = "7777",
            title = "Questionnaire",
            from = Date(),
            until = Date(),
        )

        fun String.getSurveyId(): String {
            return lmsHostUrl.newBuilder(this)!!
                .build()
                .queryParameter("surveyId")!!
        }
    }
}
