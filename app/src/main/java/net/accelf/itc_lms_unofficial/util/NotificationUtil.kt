package net.accelf.itc_lms_unofficial.util

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationManagerCompat
import net.accelf.itc_lms_unofficial.Notifications

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
        cancel(Notifications.Ids.SESSION_EXPIRED)
        cancel(Notifications.Ids.WRONG_CREDENTIALS)
    }
}
