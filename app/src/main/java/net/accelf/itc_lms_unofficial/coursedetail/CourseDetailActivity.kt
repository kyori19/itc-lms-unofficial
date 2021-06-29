package net.accelf.itc_lms_unofficial.coursedetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.accelf.itc_lms_unofficial.BaseActivity
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.network.lmsHostUrl
import net.accelf.itc_lms_unofficial.util.replaceFragment
import net.accelf.itc_lms_unofficial.util.withResponse
import okhttp3.HttpUrl

@AndroidEntryPoint
class CourseDetailActivity : BaseActivity(true), BaseActivity.ProvidesUrl {

    private val viewModel by viewModels<CourseDetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.load()
        }

        val courseDetailFragment = CourseDetailFragment.newInstance()

        viewModel.courseDetail.withResponse(this, R.string.loading_course_detail) {
            replaceFragment(courseDetailFragment)
        }
    }

    override fun url(): HttpUrl {
        return lmsHostUrl.newBuilder()
            .addPathSegments("lms/course")
            .addQueryParameter("idnumber", viewModel.courseId)
            .build()
    }

    companion object {
        const val EXTRA_COURSE_ID = "course_id"
        const val EXTRA_NOTIFY_ID = "notify_id"
        const val EXTRA_COURSE_CONTENT_MATERIAL_ID = "course_content_material_id"
        const val EXTRA_URL = "url"
        const val EXTRA_OPEN_DESCRIPTION = "open_description"

        fun intent(
            context: Context,
            courseId: String,
            notifyId: String? = null,
            materialId: String? = null,
            url: String? = null,
            openDescription: Boolean = false,
        ): Intent {
            return Intent(context, CourseDetailActivity::class.java).apply {
                putExtra(EXTRA_COURSE_ID, courseId)
                putExtra(EXTRA_NOTIFY_ID, notifyId)
                putExtra(EXTRA_COURSE_CONTENT_MATERIAL_ID, materialId)
                putExtra(EXTRA_URL, url)
                putExtra(EXTRA_OPEN_DESCRIPTION, openDescription)
            }
        }

        fun Intent.putCourseId(courseId: String): Intent {
            require(component?.className == CourseDetailActivity::class.java.name) { "Intent of CourseDetailActivity required" }
            return apply {
                putExtra(EXTRA_COURSE_ID, courseId)
            }
        }
    }
}
