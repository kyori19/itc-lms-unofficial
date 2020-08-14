package net.accelf.itc_lms_unofficial.task

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.*
import io.reactivex.Single
import net.accelf.itc_lms_unofficial.CHANNEL_ID_LMS_UPDATES
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.coursedetail.CourseDetailActivity
import net.accelf.itc_lms_unofficial.models.Update
import net.accelf.itc_lms_unofficial.models.Updates
import net.accelf.itc_lms_unofficial.network.LMS
import java.util.concurrent.TimeUnit

class PullUpdatesWorker @WorkerInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val lms: LMS
) : RxWorker(context, workerParams) {

    override fun createWork(): Single<Result> {
        return lms.getUpdates()
            .onErrorReturn {
                Updates(listOf(), it)
            }
            .map {
                if (it.throwable != null) {
                    it.throwable.printStackTrace()
                    return@map Result.failure()
                }

                it.updates.forEach { update ->
                    NotificationManagerCompat.from(context)
                        .notify(update.id.toInt(), update.toNotification())
                }
                Result.success()
            }
    }

    private fun Update.toNotification(): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID_LMS_UPDATES)
            .apply {
                setSmallIcon(R.drawable.ic_launcher_foreground)
                setContentTitle(courseName)
                setContentText(text)

                priority = NotificationCompat.PRIORITY_HIGH
                setVisibility(VISIBILITY_PUBLIC)
                setOnlyAlertOnce(true)

                setAutoCancel(true)

                val intent = CourseDetailActivity.intent(context, courseId)
                val pendingIntent = TaskStackBuilder.create(context)
                    .run {
                        addNextIntentWithParentStack(intent)
                        getPendingIntent(id.toInt(), PendingIntent.FLAG_UPDATE_CURRENT)
                    }
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
