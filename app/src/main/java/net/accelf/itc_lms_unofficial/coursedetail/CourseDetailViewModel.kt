package net.accelf.itc_lms_unofficial.coursedetail

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import net.accelf.itc_lms_unofficial.coursedetail.CourseDetailActivity.Companion.EXTRA_COURSE_CONTENT_MATERIAL_ID
import net.accelf.itc_lms_unofficial.coursedetail.CourseDetailActivity.Companion.EXTRA_COURSE_ID
import net.accelf.itc_lms_unofficial.coursedetail.CourseDetailActivity.Companion.EXTRA_NOTIFY_ID
import net.accelf.itc_lms_unofficial.models.AttendanceSend
import net.accelf.itc_lms_unofficial.models.CourseDetail
import net.accelf.itc_lms_unofficial.models.NotifyDetail
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.util.*

class CourseDetailViewModel @ViewModelInject constructor(
    private val lms: LMS,
    @Assisted private val savedState: SavedStateHandle,
) : RxAwareViewModel() {

    val courseId = savedState.get<String>(EXTRA_COURSE_ID)!!

    private val mutableCourseDetail = mutableRequestOf<CourseDetail>()
    val courseDetail: LiveData<Request<CourseDetail>> = mutableCourseDetail

    private val mutableNotifyDetail = mutableLiveDataOf<Request<NotifyDetail>?>(null)
    val notifyDetail: LiveData<Request<NotifyDetail>?> = mutableNotifyDetail

    var focusCourseContentResourceId: String?
        get() = savedState.get(EXTRA_COURSE_CONTENT_MATERIAL_ID)
        private set(value) {
            savedState.set(EXTRA_COURSE_CONTENT_MATERIAL_ID, value)
        }

    private val mutableAttendanceSend = mutableLiveDataOf<Request<AttendanceSend>?>(null)
    val attendanceSend: LiveData<Request<AttendanceSend>?> = mutableAttendanceSend

    init {
        load()

        savedState.get<String>(EXTRA_NOTIFY_ID)?.let {
            loadNotify(it)
            savedState.set(EXTRA_NOTIFY_ID, null)
        }
    }

    fun load() {
        lms.getCourseDetail(courseId)
            .toLiveData(mutableCourseDetail)
    }

    private fun usingSnackbar(ignoreAttendance: Boolean = false): Boolean {
        return mutableNotifyDetail.value != null
                || !(ignoreAttendance || mutableAttendanceSend.value == null)
    }

    fun loadNotify(notifyId: String): Boolean {
        if (usingSnackbar()) {
            return false
        }

        savedState.set(EXTRA_NOTIFY_ID, notifyId)
        lms.getNotifyDetail(courseId, notifyId)
            .toLiveData(mutableNotifyDetail)
        return true
    }

    fun closeNotify() {
        mutableNotifyDetail.postValue(null)
        savedState.set(EXTRA_NOTIFY_ID, null)
    }

    fun onCourseContentOpened() {
        focusCourseContentResourceId = null
    }

    fun prepareForSendingAttendance(attendanceId: String): Boolean {
        if (usingSnackbar()) {
            return false
        }

        lms.getAttendanceSend(courseId, attendanceId)
            .toLiveData(mutableAttendanceSend)
        return true
    }

    fun sendAttendance(password: String, comment: String): Boolean {
        if (usingSnackbar(true)) {
            return false
        }

        val form = (mutableAttendanceSend.value as Success).data
        lms.sendAttendance(form.csrf, courseId, form.id, form.sent, password, comment)
            .toLiveData(mutableAttendanceSend)
        return true
    }

    fun closeSendAttendance() {
        if (mutableAttendanceSend.value !is Success) {
            return
        }

        mutableAttendanceSend.postValue(null)
    }
}
