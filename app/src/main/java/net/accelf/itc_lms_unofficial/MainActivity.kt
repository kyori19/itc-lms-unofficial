package net.accelf.itc_lms_unofficial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.timetable.TimeTableFragment
import net.accelf.itc_lms_unofficial.util.replaceFragment
import net.accelf.itc_lms_unofficial.util.withResponse
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    @Inject
    lateinit var lms: LMS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        replaceFragment(LoadingFragment.newInstance(getString(R.string.loading_time_table)))

        lms.getTimeTable()
            .withResponse(this) {
                replaceFragment(TimeTableFragment.newInstance(it))
            }
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
