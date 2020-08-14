package net.accelf.itc_lms_unofficial.coursedetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import net.accelf.itc_lms_unofficial.BaseActivity
import net.accelf.itc_lms_unofficial.LoadingFragment
import net.accelf.itc_lms_unofficial.MainActivity
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.util.replaceFragment
import net.accelf.itc_lms_unofficial.util.withResponse
import javax.inject.Inject

@AndroidEntryPoint
class CourseDetailActivity : BaseActivity() {

    @Inject
    lateinit var lms: LMS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.getStringExtra(EXTRA_COURSE_ID) == null) {
            startActivity(
                MainActivity.intent(
                    this
                )
            )
            finish()
            return
        }

        replaceFragment(
            LoadingFragment.newInstance(
                getString(R.string.loading_course_detail)
            )
        )
        lms.getCourseDetail(intent.getStringExtra(EXTRA_COURSE_ID)!!)
            .withResponse(this) {
                replaceFragment(CourseDetailFragment.newInstance(it))
            }
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
