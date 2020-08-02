package net.accelf.itc_lms_unofficial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.timetable.TimeTableFragment
import net.accelf.itc_lms_unofficial.util.call
import net.accelf.itc_lms_unofficial.util.replaceErrorFragment
import net.accelf.itc_lms_unofficial.util.replaceFragment
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    @Inject
    lateinit var lms: LMS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        replaceFragment(LoadingFragment.newInstance(getString(R.string.loading_check_account)))

        lms.getLog()
            .call(this)
            .subscribe({
                if (it.contentLength() != 0L) {
                    replaceFragment(StartLoginFragment.newInstance())
                } else {
                    replaceFragment(LoadingFragment.newInstance(getString(R.string.loading_time_table)))
                    lms.getTimeTable()
                        .call(this)
                        .subscribe({ timeTable ->
                            replaceFragment(TimeTableFragment.newInstance(timeTable))
                        }, { throwable ->
                            replaceErrorFragment(throwable)
                        })
                }
            }, { throwable ->
                replaceErrorFragment(throwable)
            })
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
