package net.accelf.itc_lms_unofficial.util

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationManagerCompat

const val NOTIFICATION_ID_SESSION_EXPIRED = 10000001
const val NOTIFICATION_ID_WRONG_CREDENTIALS = 10000002
const val NOTIFICATION_ID_PERMISSION_REQUIRED = 20000000
const val NOTIFICATION_ID_DOWNLOAD_PROGRESS = 30000001

fun Context.notify(id: Int, notification: Notification) {
    NotificationManagerCompat.from(this).notify(id, notification)
}

fun Context.getNotifications(): Array<StatusBarNotification> {
    return getSystemService(NotificationManager::class.java)!!.activeNotifications
}

fun Context.cancelNotification(id: Int) {
    NotificationManagerCompat.from(this).cancel(id)
}

fun Context.cancelNotificationsWhichShouldBeCanceledAfterLogin() {
    NotificationManagerCompat.from(this).apply {
        cancel(NOTIFICATION_ID_SESSION_EXPIRED)
        cancel(NOTIFICATION_ID_WRONG_CREDENTIALS)
    }
}
