package net.accelf.itc_lms_unofficial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.timetable.TimeTableFragment
import net.accelf.itc_lms_unofficial.util.call
import net.accelf.itc_lms_unofficial.util.replaceErrorFragment
import net.accelf.itc_lms_unofficial.util.replaceFragment
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var lms: LMS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        replaceFragment(
            LoadingFragment.newInstance(getString(R.string.loading_check_account)),
            content.id
        )

        lms.getLog()
            .call(this)
            .subscribe({
                if (it.contentLength() != 0L) {
                    replaceFragment(StartLoginFragment.newInstance(), content.id)
                } else {
                    replaceFragment(
                        LoadingFragment.newInstance(getString(R.string.loading_time_table)),
                        content.id
                    )
                    lms.getTimeTable()
                        .call(this)
                        .subscribe({ timeTable ->
                            replaceFragment(TimeTableFragment.newInstance(timeTable), content.id)
                        }, { throwable ->
                            replaceErrorFragment(throwable, content.id)
                        })
                }
            }, { throwable ->
                replaceErrorFragment(throwable, content.id)
            })
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
