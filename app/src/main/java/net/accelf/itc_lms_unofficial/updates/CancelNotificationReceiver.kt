package net.accelf.itc_lms_unofficial.updates

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import net.accelf.itc_lms_unofficial.models.Update
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.util.getSerializableExtraCompat
import javax.inject.Inject

@AndroidEntryPoint
class CancelNotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var lms: LMS

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.also {
            val update = it.getSerializableExtraCompat<Update>(EXTRA_UPDATE)!!
            val csrf = it.getStringExtra(EXTRA_CSRF)!!

            runBlocking {
                lms.deleteUpdates(csrf, listOf(update.targetId))
            }
        }
    }

    companion object {
        private const val EXTRA_UPDATE = "update"
        private const val EXTRA_CSRF = "csrf"

        fun intent(
            context: Context,
            update: Update,
            csrf: String,
        ): Intent {
            return Intent(context, CancelNotificationReceiver::class.java)
                .apply {
                    putExtra(EXTRA_UPDATE, update)
                    putExtra(EXTRA_CSRF, csrf)
                }
        }
    }
}
