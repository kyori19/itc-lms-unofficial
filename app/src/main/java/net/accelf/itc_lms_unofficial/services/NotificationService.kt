package net.accelf.itc_lms_unofficial.services

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleService
import dagger.hilt.android.AndroidEntryPoint
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.util.call
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : LifecycleService() {

    @Inject
    lateinit var lms: LMS

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val csrf = it.getStringExtra(EXTRA_CSRF)!!
            val updateTargetId = it.getStringExtra(EXTRA_UPDATE_TARGET_ID)!!

            lms.deleteUpdates(csrf, listOf(updateTargetId))
                .call(this)
                .subscribe(
                    {},
                    { throwable ->
                        throwable.printStackTrace()
                    }
                )

            if (intent.hasExtra(EXTRA_ACTIVITY_INTENT)) {
                val pendingIntent = it.getParcelableExtra<PendingIntent>(EXTRA_ACTIVITY_INTENT)
                pendingIntent?.send()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    companion object {
        private const val EXTRA_ACTIVITY_INTENT = "activity_intent"
        private const val EXTRA_CSRF = "csrf"
        private const val EXTRA_UPDATE_TARGET_ID = "update_target_id"

        fun intent(
            context: Context,
            csrf: String,
            updateTargetId: String,
            activityIntent: PendingIntent? = null,
        ): Intent {
            return Intent(context, NotificationService::class.java)
                .apply {
                    putExtra(EXTRA_ACTIVITY_INTENT, activityIntent)
                    putExtra(EXTRA_CSRF, csrf)
                    putExtra(EXTRA_UPDATE_TARGET_ID, updateTargetId)
                }
        }
    }
}
