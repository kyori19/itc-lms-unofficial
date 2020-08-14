package net.accelf.itc_lms_unofficial.network

import io.reactivex.Single
import net.accelf.itc_lms_unofficial.models.CourseDetail
import net.accelf.itc_lms_unofficial.models.TimeTable
import net.accelf.itc_lms_unofficial.models.Updates
import okhttp3.HttpUrl
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

val lmsHostUrl = HttpUrl.Builder()
    .scheme("https")
    .host("itc-lms.ecc.u-tokyo.ac.jp")
    .build()

interface LMS {

    @GET("lms/timetable")
    fun getTimeTable(): Single<TimeTable>

    @GET("lms/course")
    fun getCourseDetail(@Query("idnumber") idNumber: String): Single<CourseDetail>

    @GET("lms/timetable/log")
    fun getLog(): Single<ResponseBody>

    @GET("updateinfo")
    fun getUpdates(): Single<Updates>

}
