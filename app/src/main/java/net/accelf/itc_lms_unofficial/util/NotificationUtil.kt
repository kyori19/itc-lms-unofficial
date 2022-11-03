package net.accelf.itc_lms_unofficial.util

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationManagerCompat
import net.accelf.itc_lms_unofficial.Notifications
import java.io.Serializable

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

@Suppress("DEPRECATION")
inline fun <reified T: Serializable> Bundle.getSerializableCompat(name: String): T? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        return getSerializable(name, T::class.java)
    }

    return getSerializable(name) as T?
}

@Suppress("DEPRECATION")
inline fun <reified T: Serializable> Intent.getSerializableExtraCompat(name: String): T? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        return getSerializableExtra(name, T::class.java)
    }

    return getSerializableExtra(name) as T?
}
