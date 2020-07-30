package net.accelf.itc_lms_unofficial.models

import net.accelf.itc_lms_unofficial.network.lmsHostUrl
import java.io.Serializable
import java.util.*

fun String.getTestParams(): Pair<String, Test.TestStatus> {
    return lmsHostUrl.newBuilder(this)!!
        .build()
        .let {
            Pair(
                it.queryParameter("examinationId")!!,
                if (it.pathSegments.last() == "takeresult") Test.TestStatus.TAKEN else Test.TestStatus.NOT_TAKEN
            )
        }
}

class Test(
    val id: String?,
    val title: String,
    val status: TestStatus,
    val from: Date,
    val until: Date
) : Serializable {
    enum class TestStatus {
        TAKEN,
        NOT_TAKEN,
        UNKNOWN
    }
}
