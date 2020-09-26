package net.accelf.itc_lms_unofficial.coursedetail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import net.accelf.itc_lms_unofficial.models.CourseDetail
import net.accelf.itc_lms_unofficial.models.NotifyDetail
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.util.Request
import net.accelf.itc_lms_unofficial.util.RxAwareViewModel
import net.accelf.itc_lms_unofficial.util.mutableLiveDataOf
import net.accelf.itc_lms_unofficial.util.mutableRequestOf

class CourseDetailViewModel @ViewModelInject constructor(
    private val lms: LMS,
) : RxAwareViewModel() {

    private val mutableCourseDetail = mutableRequestOf<CourseDetail>()
    val courseDetail: LiveData<Request<CourseDetail>> = mutableCourseDetail

    private val mutableNotifyDetail = mutableLiveDataOf<Request<NotifyDetail>?>(null)
    val notifyDetail: LiveData<Request<NotifyDetail>?> = mutableNotifyDetail

    fun load(courseId: String) {
        lms.getCourseDetail(courseId)
            .toLiveData(mutableCourseDetail)
    }

    fun loadNotify(courseId: String, notifyId: String): Boolean {
        if (mutableNotifyDetail.value != null) {
            return false
        }

        lms.getNotifyDetail(courseId, notifyId)
            .toLiveData(mutableNotifyDetail)
        return true
    }

    fun closeNotify() {
        mutableNotifyDetail.postValue(null)
    }
}
