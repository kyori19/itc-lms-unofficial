package net.accelf.itc_lms_unofficial.updates

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.webkit.WebView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_DEFAULT
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.turingcomplete.kotlinonetimepassword.GoogleAuthenticator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.accelf.itc_lms_unofficial.Notifications
import net.accelf.itc_lms_unofficial.Prefs
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.di.EncryptedDataStore
import net.accelf.itc_lms_unofficial.di.SavedCookieJar
import net.accelf.itc_lms_unofficial.login.LoginActivity
import net.accelf.itc_lms_unofficial.login.LoginHelper
import net.accelf.itc_lms_unofficial.models.Update
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.settings.PreferenceActivity
import net.accelf.itc_lms_unofficial.util.*
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

@HiltWorker
class PullUpdatesWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val lms: LMS,
    private val cookieJar: SavedCookieJar,
    private val encryptedDataStore: EncryptedDataStore,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.Main) {
        val it = runCatching { lms.getUpdates() }
            .getOrElse {
                if (it is HttpException && it.code() == 302) {
                    if (automateLogin()) {
                        return@withContext login()
                    }
                    context.notify(Notifications.Ids.SESSION_EXPIRED, expiredNotification())
                }

                return@withContext Result.failure()
            }

        context.cancelNotificationsWhichShouldBeCanceledAfterLogin()

        val updateIds = it.updates.map { update -> update.id.toInt() }
        context.getNotifications()
            .forEach { sbn ->
                if (sbn.id < Notifications.Ids.SESSION_EXPIRED && sbn.id !in updateIds) {
                    context.cancelNotification(sbn.id)
                }
            }

        it.updates.forEach { update ->
            context.notify(update.id.toInt(), update.toNotification(it.csrf))
        }

        return@withContext Result.success()
    }

    private fun automateLogin(): Boolean =
        context.defaultSharedPreference.getBoolean(Prefs.Keys.AUTOMATE_LOGIN, false)

    private fun putCredentials(credentials: Set<String>) {
        context.defaultSharedPreference.edit()
            .putStringSet(Prefs.Keys.COOKIE, credentials)
            .apply()
        cookieJar.loadCookies()
    }

    private suspend fun login(): Result {
        Log.d("PullUpdatesWorker", "Automated login")

        val helper = LoginHelper(WebView(context))
        helper.prepare()

        val username = encryptedDataStore.getString(Prefs.Keys.LOGIN_USERNAME, "")!!
        val password = encryptedDataStore.getString(Prefs.Keys.LOGIN_PASSWORD, "")!!

        runCatching { helper.login(username, password) }
            .onSuccess {
                putCredentials(it)
                return Result.retry()
            }
            .onFailure {
                if (it != LoginHelper.MFARequiredException) {
                    Log.d("PullUpdatesWorker", "Password login failed")
                    it.printStackTrace()

                    context.notify(Notifications.Ids.WRONG_CREDENTIALS, wrongCredentialsNotification())
                    return Result.failure()
                }
            }

        Log.d("PullUpdatesWorker", "Automated MFA")

        val mfaSecret = encryptedDataStore.getString(Prefs.Keys.MFA_SECRET, "")!!
        val code = GoogleAuthenticator(mfaSecret.ifEmpty { "secret" }.toByteArray()).generate()

        runCatching { helper.mfa(code) }
            .onSuccess {
                putCredentials(it)
                return Result.retry()
            }
            .onFailure {
                Log.d("PullUpdatesWorker", "MFA failed")
                it.printStackTrace()
            }

        context.notify(Notifications.Ids.WRONG_CREDENTIALS, wrongCredentialsNotification())
        return Result.failure()
    }

    private fun Update.toNotification(csrf: String): Notification {
        return NotificationCompat.Builder(context, Notifications.Channels.LMS_UPDATES)
            .apply {
                setSmallIcon(R.drawable.ic_launcher_foreground)
                setContentTitle(courseName)
                setContentText(text)
                setStyle(NotificationCompat.BigTextStyle().bigText(text))
                createdAt?.let { setWhen(it.time) }

                priority = NotificationCompat.PRIORITY_HIGH
                setVisibility(VISIBILITY_PUBLIC)
                setOnlyAlertOnce(true)

                setAutoCancel(true)

                val openIntent = toPendingIntent(
                    context,
                    csrf,
                    80000000 + id.toInt(),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT,
                )
                setContentIntent(openIntent)

                val cancelIntent = CancelNotificationReceiver.intent(context, this@toNotification, csrf)
                val pendingCancelIntent = PendingIntent.getBroadcast(
                    context,
                    90000000 + id.toInt(),
                    cancelIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT,
                )
                setDeleteIntent(pendingCancelIntent)
            }.build()
    }

    private fun expiredNotification(): Notification {
        return NotificationCompat.Builder(context, Notifications.Channels.ERRORS)
            .apply {
                setSmallIcon(R.drawable.ic_launcher_foreground)
                setContentTitle(context.getString(R.string.notify_title_session_expired))
                setContentText(context.getString(R.string.notify_text_request_login))

                priority = PRIORITY_DEFAULT
                setVisibility(VISIBILITY_PUBLIC)
                setOnlyAlertOnce(true)

                setAutoCancel(true)

                val intent = Intent(context, LoginActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    context, Notifications.Ids.SESSION_EXPIRED,
                    intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                )
                setContentIntent(pendingIntent)
            }.build()
    }

    private fun wrongCredentialsNotification(): Notification {
        return NotificationCompat.Builder(context, Notifications.Channels.ERRORS)
            .apply {
                setSmallIcon(R.drawable.ic_launcher_foreground)
                setContentTitle(context.getString(R.string.notify_title_wrong_credentials))
                setContentText(context.getString(R.string.notify_text_wrong_credentials))

                priority = PRIORITY_DEFAULT
                setVisibility(VISIBILITY_PUBLIC)
                setOnlyAlertOnce(true)

                setAutoCancel(true)

                val intent = Intent(context, PreferenceActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    context, Notifications.Ids.WRONG_CREDENTIALS,
                    intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                )
                setContentIntent(pendingIntent)
            }.build()
    }

    companion object {
        fun enqueue(context: Context, replace: Boolean = false) {
            val request = PeriodicWorkRequestBuilder<PullUpdatesWorker>(15, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    this::class.java.name,
                    if (replace) {
                        ExistingPeriodicWorkPolicy.REPLACE
                    } else {
                        ExistingPeriodicWorkPolicy.KEEP
                    },
                    request
                )
        }
    }
}
