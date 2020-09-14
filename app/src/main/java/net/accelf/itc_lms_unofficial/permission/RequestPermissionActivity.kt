package net.accelf.itc_lms_unofficial.permission

import android.app.Activity
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import net.accelf.itc_lms_unofficial.BaseActivity
import net.accelf.itc_lms_unofficial.CHANNEL_ID_ERRORS
import net.accelf.itc_lms_unofficial.NOTIFICATION_ID_PERMISSION_REQUIRED
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.util.replaceFragment

class RequestPermissionActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.hasExtra(EXTRA_PERMISSION_ID)) {
            val permissionId = intent.getIntExtra(EXTRA_PERMISSION_ID, 0)
            val permission = Permission.fromId(permissionId)

            if (!shouldShowRequestPermissionRationale(permission.androidName)) {
                request(permission)
                return
            }

            replaceFragment(RequestPermissionFragment.newInstance(permissionId))
        }
    }

    fun request(permission: Permission) {
        permission.request(this) {
            setResult(if (it) Activity.RESULT_OK else Activity.RESULT_CANCELED)
            finish()
        }
    }

    companion object {

        private const val EXTRA_PERMISSION_ID = "permission_id"

        fun intent(context: Context, permission: Permission): Intent {
            return Intent(context, RequestPermissionActivity::class.java).apply {
                putExtra(EXTRA_PERMISSION_ID, permission.id)
            }
        }

        fun permissionRequiredNotification(
            context: Context,
            permission: Permission,
        ): Pair<Int, Notification> {
            val id = NOTIFICATION_ID_PERMISSION_REQUIRED + permission.id
            return id to NotificationCompat.Builder(context, CHANNEL_ID_ERRORS)
                .apply {
                    setSmallIcon(R.drawable.ic_info)
                    setContentTitle(context.getString(R.string.notify_title_permission_required))
                    setContentText(context.getString(R.string.notify_text_request_permission))

                    priority = NotificationCompat.PRIORITY_DEFAULT
                    setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    setOnlyAlertOnce(true)

                    setAutoCancel(true)

                    val intent = intent(context, permission)
                    val pendingIntent = PendingIntent.getActivity(
                        context, id,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    setContentIntent(pendingIntent)
                }.build()
        }
    }
}
