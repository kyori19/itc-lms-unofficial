package net.accelf.itc_lms_unofficial.task

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.os.Handler
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_DEFAULT
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.TaskStackBuilder
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.*
import io.reactivex.Single
import net.accelf.itc_lms_unofficial.*
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.coursedetail.CourseDetailActivity
import net.accelf.itc_lms_unofficial.coursedetail.CourseDetailActivity.Companion.putCourseId
import net.accelf.itc_lms_unofficial.di.EncryptedDataStore
import net.accelf.itc_lms_unofficial.di.SavedCookieJar
import net.accelf.itc_lms_unofficial.models.Update
import net.accelf.itc_lms_unofficial.models.Updates
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.reportdetail.ReportDetailActivity
import net.accelf.itc_lms_unofficial.services.NotificationService
import net.accelf.itc_lms_unofficial.settings.PreferenceActivity
import net.accelf.itc_lms_unofficial.settings.PreferenceFragment.Companion.PREF_AUTOMATE_LOGIN
import net.accelf.itc_lms_unofficial.settings.PreferenceFragment.Companion.PREF_LOGIN_PASSWORD
import net.accelf.itc_lms_unofficial.settings.PreferenceFragment.Companion.PREF_LOGIN_USERNAME
import net.accelf.itc_lms_unofficial.util.NOTIFICATION_ID_SESSION_EXPIRED
import net.accelf.itc_lms_unofficial.util.NOTIFICATION_ID_WRONG_CREDENTIALS
import net.accelf.itc_lms_unofficial.util.defaultSharedPreference
import net.accelf.itc_lms_unofficial.util.notify
import okhttp3.HttpUrl.Companion.toHttpUrl
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

class PullUpdatesWorker @WorkerInject constructor(
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
                            context.notify(NOTIFICATION_ID_WRONG_CREDENTIALS,
                                wrongCredentialsNotification())
                            return@map Result.failure()
                        }

                        if (context.defaultSharedPreference.getBoolean(PREF_AUTOMATE_LOGIN,
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
                                                        PREF_LOGIN_USERNAME, "")
                                                }'", null)
                                                evaluateJavascript("document.getElementById('passwordInput').value = '${
                                                    encryptedDataStore.getString(
                                                        PREF_LOGIN_PASSWORD, "")
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
                                                    .putStringSet(PREF_COOKIE,
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

                        context.notify(NOTIFICATION_ID_SESSION_EXPIRED, expiredNotification())
                        return@map Result.failure()
                    }
                    printStackTrace()
                    return@map Result.failure()
                }

                afterLogin = false

                it.updates.forEach { update ->
                    context.notify(update.id.toInt(), update.toNotification(it.csrf))
                }
                Result.success()
            }
    }

    private fun Update.toNotification(csrf: String): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID_LMS_UPDATES)
            .apply {
                setSmallIcon(R.drawable.ic_launcher_foreground)
                setContentTitle(courseName)
                setContentText(text)
                createdAt?.let { setWhen(it.time) }

                priority = NotificationCompat.PRIORITY_HIGH
                setVisibility(VISIBILITY_PUBLIC)
                setOnlyAlertOnce(true)

                setAutoCancel(true)

                val activityIntent = when (contentType) {
                    Update.ContentType.NOTIFY -> CourseDetailActivity.intent(context,
                        courseId,
                        contentId)
                    Update.ContentType.REPORT -> ReportDetailActivity.intent(context,
                        courseId,
                        contentId)
                    else -> CourseDetailActivity.intent(context, courseId)
                }
                val stackedIntent = TaskStackBuilder.create(context)
                    .run {
                        addNextIntentWithParentStack(activityIntent)
                        if (contentType == Update.ContentType.REPORT) {
                            editIntentAt(intents.indexOfFirst { it.component?.shortClassName == ".coursedetail.CourseDetailActivity" })
                                ?.putCourseId(courseId)
                        }
                        getPendingIntent(id.toInt(), PendingIntent.FLAG_CANCEL_CURRENT)
                    }

                val openIntent =
                    NotificationService.intent(context, csrf, targetId, stackedIntent!!)
                val pendingOpenIntent = PendingIntent.getService(context,
                    80000000 + id.toInt(),
                    openIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT)
                setContentIntent(pendingOpenIntent)

                val cancelIntent = NotificationService.intent(context, csrf, targetId)
                val pendingCancelIntent = PendingIntent.getService(context,
                    90000000 + id.toInt(),
                    cancelIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT)
                setDeleteIntent(pendingCancelIntent)
            }.build()
    }

    private fun expiredNotification(): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID_ERRORS)
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
                    context, NOTIFICATION_ID_SESSION_EXPIRED,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT
                )
                setContentIntent(pendingIntent)
            }.build()
    }

    private fun wrongCredentialsNotification(): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID_ERRORS)
            .apply {
                setSmallIcon(R.drawable.ic_launcher_foreground)
                setContentTitle(context.getString(R.string.notify_title_wrong_credentials))
                setContentText(context.getString(R.string.notify_text_wrong_credentials))

                priority = PRIORITY_DEFAULT
                setVisibility(VISIBILITY_PUBLIC)
                setOnlyAlertOnce(true)

                setAutoCancel(true)

                val intent = PreferenceActivity.intent(context)
                val pendingIntent = PendingIntent.getActivity(
                    context, NOTIFICATION_ID_WRONG_CREDENTIALS,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT
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
