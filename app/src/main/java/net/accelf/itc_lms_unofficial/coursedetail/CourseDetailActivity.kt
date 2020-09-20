package net.accelf.itc_lms_unofficial.coursedetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import net.accelf.itc_lms_unofficial.BaseActivity
import net.accelf.itc_lms_unofficial.LoadingFragment
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.network.lmsHostUrl
import net.accelf.itc_lms_unofficial.util.replaceFragment
import net.accelf.itc_lms_unofficial.util.withResponse
import okhttp3.HttpUrl
import javax.inject.Inject

@AndroidEntryPoint
class CourseDetailActivity : BaseActivity(false), BaseActivity.ProvidesUrl {

    private lateinit var courseId: String
    private var notifyId: String? = null

    @Inject
    lateinit var lms: LMS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        courseId = intent.getStringExtra(EXTRA_COURSE_ID)!!
        notifyId = intent.getStringExtra(EXTRA_NOTIFY_ID)

        replaceFragment(
            LoadingFragment.newInstance(
                getString(R.string.loading_course_detail)
            )
        )
        lms.getCourseDetail(courseId)
            .withResponse(this) {
                replaceFragment(CourseDetailFragment.newInstance(it, notifyId))
            }
    }

    override fun url(): HttpUrl {
        return lmsHostUrl.newBuilder()
            .addPathSegments("lms/course")
            .addQueryParameter("idnumber", courseId)
            .build()
    }

    companion object {
        private const val EXTRA_COURSE_ID = "course_id"
        private const val EXTRA_NOTIFY_ID = "notify_id"

        fun intent(context: Context, courseId: String, notifyId: String? = null): Intent {
            return Intent(context, CourseDetailActivity::class.java).apply {
                putExtra(EXTRA_COURSE_ID, courseId)
                putExtra(EXTRA_NOTIFY_ID, notifyId)
            }
        }

        fun Intent.putCourseId(courseId: String): Intent {
            require(component?.shortClassName == ".coursedetail.CourseDetailActivity") { "Intent of CourseDetailActivity required" }
            return apply {
                putExtra(EXTRA_COURSE_ID, courseId)
            }
        }
    }
}
