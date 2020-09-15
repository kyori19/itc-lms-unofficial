package net.accelf.itc_lms_unofficial.util

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationManagerCompat

const val NOTIFICATION_ID_SESSION_EXPIRED = 10000001
const val NOTIFICATION_ID_WRONG_CREDENTIALS = 10000002
const val NOTIFICATION_ID_PERMISSION_REQUIRED = 20000000
const val NOTIFICATION_ID_DOWNLOAD_PROGRESS = 30000001

fun Context.notify(id: Int, notification: Notification) {
    NotificationManagerCompat.from(this).notify(id, notification)
}

private fun Context.cancelNotification(id: Int) {
    NotificationManagerCompat.from(this).cancel(id)
}

fun Context.cancelExpiredNotification() {
    cancelNotification(NOTIFICATION_ID_SESSION_EXPIRED)
}
