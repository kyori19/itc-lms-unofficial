package net.accelf.itc_lms_unofficial.network

import io.reactivex.Single
import net.accelf.itc_lms_unofficial.models.*
import okhttp3.HttpUrl
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Streaming

val lmsHostUrl = HttpUrl.Builder()
    .scheme("https")
    .host("itc-lms.ecc.u-tokyo.ac.jp")
    .build()

interface LMS {

    @GET("lms/timetable")
    fun getTimeTable(): Single<TimeTable>

    @GET("lms/course")
    fun getCourseDetail(@Query("idnumber") idNumber: String): Single<CourseDetail>

    @GET("lms/coursetop/information/listdetail")
    fun getNotifyDetail(@Query("informationId") notifyId: String): Single<NotifyDetail>

    @GET("lms/course/material/tempfile")
    fun getFileId(
        @Query("idnumber") courseId: String,
        @Query("materialId") materialId: String,
        @Query("resourceId") resourceId: String,
        @Query("fileName") fileName: String,
        @Query("objectName") objectName: String,
    ): Single<String>

    @Streaming
    @GET("lms/course/material/setfiledown/download")
    fun downloadMaterialFile(
        @Query("fileId") fileId: String,
        @Query("contentId") materialId: String,
        @Query("endDate") endDate: String,
        @Query("fileName") name: String = "file",
    ): Single<ResponseBody>

    @GET("lms/timetable/log")
    fun getLog(): Single<ResponseBody>

    @GET("updateinfo")
    fun getUpdates(): Single<Updates>

    @GET("lms/course/report/submission")
    fun getReportDetail(
        @Query("idnumber") idNumber: String,
        @Query("reportId") reportId: String,
    ): Single<ReportDetail>

    @Streaming
    @GET("lms/course/report/submission_download/download")
    fun downloadReportFile(
        @Query("objectName") objectName: String,
        @Query("downloadFileName") name: String = "file",
    ): Single<ResponseBody>

}
