package net.accelf.itc_lms_unofficial.task

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_DEFAULT
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.hilt.work.HiltWorker
import androidx.work.*
import androidx.work.rxjava3.RxWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.core.Single
import net.accelf.itc_lms_unofficial.LoginActivity
import net.accelf.itc_lms_unofficial.Notifications
import net.accelf.itc_lms_unofficial.Prefs
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.di.EncryptedDataStore
import net.accelf.itc_lms_unofficial.di.SavedCookieJar
import net.accelf.itc_lms_unofficial.models.Update
import net.accelf.itc_lms_unofficial.models.Updates
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.services.NotificationService
import net.accelf.itc_lms_unofficial.settings.PreferenceActivity
import net.accelf.itc_lms_unofficial.util.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

@HiltWorker
class PullUpdatesWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val lms: LMS,
    private val cookieJar: SavedCookieJar,
    private val encryptedDataStore: EncryptedDataStore,
) : RxWorker(context, workerParams) {

    override fun createWork(): Single<Result> {
        return lms.getUpdates()
            .onErrorReturn {
                Updates("", listOf(), it)
            }
            .map {
                it.throwable?.run {
                    if (this is HttpException && code() == 302) {
                        if (afterLogin) {
                            context.notify(Notifications.Ids.WRONG_CREDENTIALS,
                                wrongCredentialsNotification())
                            return@map Result.failure()
                        }

                        if (context.defaultSharedPreference.getBoolean(Prefs.Keys.AUTOMATE_LOGIN,
                                false)
                        ) {
                            Handler(context.mainLooper).post {
                                WebView(context).apply {
                                    @SuppressLint("SetJavaScriptEnabled")
                                    settings.javaScriptEnabled = true
                                    webViewClient = object : WebViewClient() {
                                        override fun onPageFinished(view: WebView?, url: String?) {
                                            super.onPageFinished(view, url)
                                            if (url?.toHttpUrl()?.host == "sts.adm.u-tokyo.ac.jp") {
                                                evaluateJavascript("document.getElementById('userNameInput').value = '${
                                                    encryptedDataStore.getString(
                                                        Prefs.Keys.LOGIN_USERNAME, "")
                                                }'", null)
                                                evaluateJavascript("document.getElementById('passwordInput').value = '${
                                                    encryptedDataStore.getString(
                                                        Prefs.Keys.LOGIN_PASSWORD, "")
                                                }'", null)
                                                evaluateJavascript("document.getElementById('submitButton').click()",
                                                    null)
                                            }
                                        }

                                        override fun shouldOverrideUrlLoading(
                                            view: WebView?,
                                            request: WebResourceRequest?,
                                        ): Boolean {
                                            if (request?.url?.path.equals("/lms/timetable")) {
                                                context.defaultSharedPreference.edit()
                                                    .putStringSet(Prefs.Keys.COOKIE,
                                                        CookieManager.getInstance()
                                                            .getCookie(request?.url.toString())
                                                            .split(";").toSet())
                                                    .apply()
                                                cookieJar.loadCookies()
                                                enqueue(context, true)
                                                return true
                                            }
                                            return super.shouldOverrideUrlLoading(view, request)
                                        }
                                    }
                                    loadUrl("https://itc-lms.ecc.u-tokyo.ac.jp/saml/login?disco=true")
                                }
                            }

                            afterLogin = true
                            return@map Result.success()
                        }

                        context.notify(Notifications.Ids.SESSION_EXPIRED, expiredNotification())
                        return@map Result.failure()
                    }
                    printStackTrace()
                    return@map Result.failure()
                }
                context.cancelNotificationsWhichShouldBeCanceledAfterLogin()

                afterLogin = false

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
                Result.success()
            }
    }

    @SuppressLint("LaunchActivityFromNotification")
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

                val openIntent =
                    NotificationService.intent(context, this@toNotification, csrf, false)
                val pendingOpenIntent = PendingIntent.getService(
                    context,
                    80000000 + id.toInt(),
                    openIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT,
                )
                setContentIntent(pendingOpenIntent)

                val cancelIntent =
                    NotificationService.intent(context, this@toNotification, csrf, true)
                val pendingCancelIntent = PendingIntent.getService(
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

                val intent = LoginActivity.intent(context)
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
        private var afterLogin = false

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
