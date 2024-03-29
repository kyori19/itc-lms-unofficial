package net.accelf.itc_lms_unofficial.models

import net.accelf.itc_lms_unofficial.network.lmsHostUrl
import java.io.Serializable
import java.util.*

class Test(
    val id: String?,
    val title: String,
    val status: TestStatus,
    val from: Date,
    val until: Date
) : Serializable {

    fun getTakeUrl(courseId: String) =
        lmsHostUrl.newBuilder("/lms/course/examination/taketop")!!
            .addQueryParameter("idnumber", courseId)
            .addQueryParameter("examinationId", id)
            .build()
            .toString()

    enum class TestStatus {
        TAKEN,
        NOT_TAKEN,
        UNKNOWN
    }

    companion object {
        val sample = Test(
            id = "11111",
            title = "Test Title",
            status = TestStatus.TAKEN,
            from = Date(),
            until = Date(),
        )

        fun String.getTestParams(): Pair<String, TestStatus> {
            return lmsHostUrl.newBuilder(this)!!
                .build()
                .let {
                    Pair(
                        it.queryParameter("examinationId")!!,
                        if (it.pathSegments.last() == "takeresult") TestStatus.TAKEN else TestStatus.NOT_TAKEN
                    )
                }
        }
    }
}
