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
class CourseDetailActivity : BaseActivity(), BaseActivity.ProvidesUrl {

    private lateinit var courseId: String

    @Inject
    lateinit var lms: LMS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        courseId = intent.getStringExtra(EXTRA_COURSE_ID)!!

        replaceFragment(
            LoadingFragment.newInstance(
                getString(R.string.loading_course_detail)
            )
        )
        lms.getCourseDetail(courseId)
            .withResponse(this) {
                replaceFragment(CourseDetailFragment.newInstance(it))
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

        fun intent(context: Context, id: String): Intent {
            return Intent(context, CourseDetailActivity::class.java).apply {
                putExtra(EXTRA_COURSE_ID, id)
            }
        }
    }
}
