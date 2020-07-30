package net.accelf.itc_lms_unofficial.coursedetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.accelf.itc_lms_unofficial.LoadingFragment
import net.accelf.itc_lms_unofficial.MainActivity
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.network.lmsClient
import net.accelf.itc_lms_unofficial.util.call
import net.accelf.itc_lms_unofficial.util.replaceErrorFragment
import net.accelf.itc_lms_unofficial.util.replaceFragment

class CourseDetailActivity : AppCompatActivity() {
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
        val lms = lmsClient()
        lms.getCourseDetail(intent.getStringExtra(EXTRA_COURSE_ID)!!)
            .call(this)
            .subscribe({ courseDetail ->
                replaceFragment(
                    CourseDetailFragment.newInstance(
                        courseDetail
                    )
                )
            }, { throwable ->
                replaceErrorFragment(throwable)
            })
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
