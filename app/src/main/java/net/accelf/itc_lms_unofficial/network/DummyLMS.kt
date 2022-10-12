package net.accelf.itc_lms_unofficial.network

import io.reactivex.rxjava3.core.Single
import net.accelf.itc_lms_unofficial.models.*
import okhttp3.ResponseBody
import java.util.*

class DummyLMS : LMS {
    override fun getInformation(): Single<Information> = Single.just(Information(""))

    override fun getTimeTable(year: String, term: String): Single<TimeTable> = Single.just(
        TimeTable(
            name = "user",
            courses = listOf(listOf(listOf())),
            years = listOf(),
            terms = listOf(),
            since = Date(),
            until = Date(),
        )
    )

    override fun getCourseDetail(courseId: String): Single<CourseDetail> {
        TODO("Not yet implemented")
    }

    override fun getNotifyDetail(courseId: String, notifyId: String): Single<NotifyDetail> {
        TODO("Not yet implemented")
    }

    override fun getAttendanceSend(courseId: String, attendanceId: String): Single<AttendanceSend> {
        TODO("Not yet implemented")
    }

    override fun sendAttendance(
        csrf: String,
        courseId: String,
        attendanceId: String,
        sent: Boolean,
        password: String,
        comment: String,
    ): Single<AttendanceSend> {
        TODO("Not yet implemented")
    }

    override fun getFileId(
        courseId: String,
        materialId: String,
        resourceId: String,
        fileName: String,
        objectName: String,
    ): Single<String> {
        TODO("Not yet implemented")
    }

    override fun downloadMaterialFile(
        fileId: String,
        courseId: String,
        materialId: String,
        endDate: String,
        name: String,
    ): Single<ResponseBody> {
        TODO("Not yet implemented")
    }

    override suspend fun getUpdates(): Updates {
        TODO("Not yet implemented")
    }

    override fun deleteUpdates(
        csrf: String,
        targetIds: List<String>,
        method: String,
    ): Single<ResponseBody> {
        TODO("Not yet implemented")
    }

    override fun getReportDetail(idNumber: String, reportId: String): Single<ReportDetail> {
        TODO("Not yet implemented")
    }

    override fun downloadReportFile(
        objectName: String,
        courseId: String,
        name: String,
    ): Single<ResponseBody> {
        TODO("Not yet implemented")
    }

    override fun getSettings(): Single<Settings> = Single.just(
        Settings(
            "",
            listOf(),
            "",
            "",
            "",
            listOf(),
            listOf(),
        )
    )

    override fun updateSettings(
        csrf: String,
        language: String,
        mailType: String,
        mailAddress1: String,
        mailAddress2: String,
        notifyForward: String,
        updateForward: String,
        method: String,
    ): Single<ResponseBody> {
        TODO("Not yet implemented")
    }
}
