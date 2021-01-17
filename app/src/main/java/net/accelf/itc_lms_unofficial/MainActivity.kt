package net.accelf.itc_lms_unofficial

import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.accelf.itc_lms_unofficial.network.lmsHostUrl
import net.accelf.itc_lms_unofficial.timetable.TimeTableFragment
import net.accelf.itc_lms_unofficial.timetable.TimeTableViewModel
import net.accelf.itc_lms_unofficial.util.replaceFragment
import net.accelf.itc_lms_unofficial.util.withResponse
import okhttp3.HttpUrl

@AndroidEntryPoint
class MainActivity : BaseActivity(true), BaseActivity.ProvidesUrl {

    private val viewModel by viewModels<TimeTableViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.load()
        }

        val timeTableFragment = TimeTableFragment.newInstance()

        viewModel.timeTable.withResponse(this, R.string.loading_time_table) {
            replaceFragment(timeTableFragment)
        }
    }

    override fun url(): HttpUrl {
        return lmsHostUrl.newBuilder()
            .addPathSegments("lms/timetable")
            .build()
    }
}
