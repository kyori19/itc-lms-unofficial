package net.accelf.itc_lms_unofficial.reportdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import net.accelf.itc_lms_unofficial.BaseActivity
import net.accelf.itc_lms_unofficial.LoadingFragment
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.network.lmsHostUrl
import net.accelf.itc_lms_unofficial.util.replaceFragment
import net.accelf.itc_lms_unofficial.util.withResponse
import okhttp3.HttpUrl

@AndroidEntryPoint
class ReportDetailActivity : BaseActivity(false), BaseActivity.ProvidesUrl {

    private lateinit var courseId: String
    private lateinit var reportId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        courseId = intent.getStringExtra(EXTRA_COURSE_ID)!!
        reportId = intent.getStringExtra(EXTRA_REPORT_ID)!!

        replaceFragment(LoadingFragment.newInstance(getString(R.string.loading_report_detail)))
        lms.getReportDetail(courseId, reportId)
            .withResponse(this) {
                replaceFragment(ReportDetailFragment.newInstance(it))
            }
    }

    override fun url(): HttpUrl {
        return lmsHostUrl.newBuilder()
            .addPathSegments("lms/course/report/submission")
            .addQueryParameter("idnumber", courseId)
            .addQueryParameter("reportId", reportId)
            .build()
    }

    companion object {
        private const val EXTRA_COURSE_ID = "course_id"
        private const val EXTRA_REPORT_ID = "report_id"

        fun intent(context: Context, courseId: String, reportId: String): Intent {
            return Intent(context, ReportDetailActivity::class.java).apply {
                putExtra(EXTRA_COURSE_ID, courseId)
                putExtra(EXTRA_REPORT_ID, reportId)
            }
        }
    }
}
