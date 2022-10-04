package net.accelf.itc_lms_unofficial.services

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleService
import dagger.hilt.android.AndroidEntryPoint
import net.accelf.itc_lms_unofficial.models.Update
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.util.call
import javax.inject.Inject

@AndroidEntryPoint
class DeleteNotificationService : LifecycleService() {

    @Inject
    lateinit var lms: LMS

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.also {
            val update = it.getSerializableExtra(EXTRA_UPDATE) as Update
            val csrf = it.getStringExtra(EXTRA_CSRF)!!

            lms.deleteUpdates(csrf, listOf(update.targetId))
                .call(this)
                .subscribe(
                    {},
                    { throwable ->
                        throwable.printStackTrace()
                    }
                )
        }

        return super.onStartCommand(intent, flags, startId)
    }

    companion object {
        private const val EXTRA_UPDATE = "update"
        private const val EXTRA_CSRF = "csrf"

        fun intent(
            context: Context,
            update: Update,
            csrf: String,
        ): Intent {
            return Intent(context, this::class.java)
                .apply {
                    putExtra(EXTRA_UPDATE, update)
                    putExtra(EXTRA_CSRF, csrf)
                }
        }
    }
}
