package net.accelf.itc_lms_unofficial.network

import io.reactivex.rxjava3.core.Single
import net.accelf.itc_lms_unofficial.models.*
import okhttp3.HttpUrl
import okhttp3.ResponseBody
import retrofit2.http.*

val lmsHostUrl = HttpUrl.Builder()
    .scheme("https")
    .host("itc-lms.ecc.u-tokyo.ac.jp")
    .build()

interface LMS {

    @GET("login")
    fun getInformation(): Single<Information>

    @GET("lms/timetable")
    fun getTimeTable(
        @Query("risyunen") year: String = "",
        @Query("kikanCd") term: String = "",
    ): Single<TimeTable>

    @GET("lms/course")
    fun getCourseDetail(@Query("idnumber") courseId: String): Single<CourseDetail>

    @GET("lms/coursetop/information/listdetail")
    fun getNotifyDetail(
        @Query("idnumber") courseId: String,
        @Query("informationId") notifyId: String,
    ): Single<NotifyDetail>

    @GET("lms/course/attendances/send")
    fun getAttendanceSend(
        @Query("idnumber") courseId: String,
        @Query("attendanceId") attendanceId: String,
    ): Single<AttendanceSend>

    @POST("lms/course/attendances/send")
    @FormUrlEncoded
    fun sendAttendance(
        @Field("_csrf") csrf: String,
        @Field("idnumber") courseId: String,
        @Field("attendanceId") attendanceId: String,
        @Field("isSent") sent: Boolean,
        @Field("oneTimePass") password: String,
        @Field("comment") comment: String,
    ): Single<AttendanceSend>

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
        @Query("idnumber") courseId: String,
        @Query("contentId") materialId: String,
        @Query("endDate") endDate: String,
        @Query("fileName") name: String = "file",
    ): Single<ResponseBody>

    @GET("updateinfo")
    suspend fun getUpdates(): Updates

    @POST("updateinfo")
    @FormUrlEncoded
    fun deleteUpdates(
        @Field("_csrf") csrf: String,
        @Field("deleteUpdateInfoList") targetIds: List<String>,
        @Field("_method") method: String = "delete",
    ): Single<Updates>

    @GET("lms/course/report/submission")
    fun getReportDetail(
        @Query("idnumber") idNumber: String,
        @Query("reportId") reportId: String,
    ): Single<ReportDetail>

    @Streaming
    @GET("lms/course/report/submission_download/download")
    fun downloadReportFile(
        @Query("objectName") objectName: String,
        @Query("idnumber") courseId: String,
        @Query("downloadFileName") name: String = "file",
    ): Single<ResponseBody>

    @GET("settings")
    fun getSettings(): Single<Settings>

    @POST("settings")
    @FormUrlEncoded
    fun updateSettings(
        @Field("_csrf") csrf: String,
        @Field("langCd") language: String,
        @Field("mailType") mailType: String,
        @Field("mailAddress1") mailAddress1: String,
        @Field("mailAddress2") mailAddress2: String,
        @Field("forwardId11") notifyForward: String,
        @Field("forwardId110") updateForward: String,
        @Field("_method") method: String = "put",
    ): Single<ResponseBody>
}
