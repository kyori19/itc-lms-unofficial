package net.accelf.itc_lms_unofficial.coursedetail

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import net.accelf.itc_lms_unofficial.coursedetail.CourseDetailActivity.Companion.EXTRA_COURSE_CONTENT_MATERIAL_ID
import net.accelf.itc_lms_unofficial.coursedetail.CourseDetailActivity.Companion.EXTRA_COURSE_ID
import net.accelf.itc_lms_unofficial.coursedetail.CourseDetailActivity.Companion.EXTRA_NOTIFY_ID
import net.accelf.itc_lms_unofficial.models.CourseDetail
import net.accelf.itc_lms_unofficial.models.NotifyDetail
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.util.Request
import net.accelf.itc_lms_unofficial.util.RxAwareViewModel
import net.accelf.itc_lms_unofficial.util.mutableLiveDataOf
import net.accelf.itc_lms_unofficial.util.mutableRequestOf

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

    fun loadNotify(notifyId: String): Boolean {
        if (mutableNotifyDetail.value != null) {
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
}
