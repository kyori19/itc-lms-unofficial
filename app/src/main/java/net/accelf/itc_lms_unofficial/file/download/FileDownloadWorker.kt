package net.accelf.itc_lms_unofficial.file.download

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat.*
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.*
import com.google.gson.Gson
import io.reactivex.Single
import net.accelf.itc_lms_unofficial.Notifications
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.file.download.DownloadDialogResult.Companion.fromJsonToResult
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.permission.Permission
import net.accelf.itc_lms_unofficial.permission.RequestPermissionActivity
import net.accelf.itc_lms_unofficial.util.fromJson
import net.accelf.itc_lms_unofficial.util.notify
import net.accelf.itc_lms_unofficial.util.readWithProgress
import net.accelf.itc_lms_unofficial.view.WorkersAdapter.Companion.DATA_MESSAGE
import java.util.concurrent.atomic.AtomicInteger

class FileDownloadWorker @WorkerInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val lms: LMS,
    private val gson: Gson,
    private val notificationId: AtomicInteger,
) : RxWorker(context, workerParams) {

    override fun createWork(): Single<Result> {
        val downloadable =
            gson.fromJson<Downloadable>(inputData.getString(DATA_DOWNLOADABLE))
        val dialogResult = gson.fromJsonToResult(inputData.getString(DATA_DIALOG_RESULT))
        val notificationManager = NotificationManagerCompat.from(context)

        return downloadable.download(lms)
            .map {
                val permission = Permission.WRITE_EXTERNAL_STORAGE
                if (!permission.granted(context)) {
                    val (id, notification) = RequestPermissionActivity.permissionRequiredNotification(
                        context,
                        permission)
                    notificationManager.notify(id, notification)

                    val data = Data.Builder()
                        .putString(DATA_MESSAGE,
                            context.getString(R.string.notify_title_permission_required))
                        .build()

                    return@map Result.failure(data)
                }

                val builder = Builder(context, Notifications.Channels.DOWNLOADS)
                    .apply {
                        setSmallIcon(R.drawable.ic_download)
                        setContentTitle(downloadable.file.fileName)

                        priority = PRIORITY_LOW
                        setVisibility(VISIBILITY_PUBLIC)
                        setOngoing(true)
                    }
                val downloadNotificationId =
                    Notifications.Ids.DOWNLOAD_PROGRESS + notificationId.incrementAndGet()
                context.notify(downloadNotificationId, builder.build())

                val fullLength = it.contentLength()
                val bytes = it.byteStream().readWithProgress { readBytes ->
                    val progress = readBytes.toFloat() / fullLength
                    val data = Data.Builder()
                        .putString(DATA_MESSAGE,
                            "Downloading ${downloadable.file.fileName}: ${progress * 100}%")
                        .build()
                    setCompletableProgress(data).subscribe()

                    builder.setProgress(Int.MAX_VALUE, (progress * Int.MAX_VALUE).toInt(), false)
                    context.notify(downloadNotificationId, builder.build())
                }

                builder.setProgress(Int.MAX_VALUE, Int.MAX_VALUE, true)
                context.notify(downloadNotificationId, builder.build())

                val mime = "${it.contentType()?.type}/${it.contentType()?.subtype}"
                val file = dialogResult.writeToFile(context, mime, bytes)

                val data = Data.Builder()
                    .putString(DATA_MESSAGE, "Downloaded ${file.name}")
                    .build()

                builder.apply {
                    setContentText(context.getString(R.string.notify_text_downloaded))

                    setProgress(0, 0, false)
                    setOngoing(false)
                    setAutoCancel(true)

                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        setDataAndType(file.uri, mime)
                    }
                    val chooser = Intent.createChooser(intent, file.name)
                    val pendingIntent =
                        PendingIntent.getActivity(context, downloadNotificationId, chooser, 0)
                    setContentIntent(pendingIntent)
                }
                context.notify(downloadNotificationId, builder.build())

                Result.success(data)
            }
    }

    companion object {
        private const val DATA_DOWNLOADABLE = "downloadable"
        private const val DATA_DIALOG_RESULT = "dialog_result"

        private fun enqueue(context: Context, data: Data) {
            val request = OneTimeWorkRequestBuilder<FileDownloadWorker>()
                .setInputData(data)
                .build()

            WorkManager.getInstance(context).enqueue(request)
        }

        fun enqueue(
            context: Context, gson: Gson, downloadable: Downloadable,
            dialogResult: DownloadDialogResult,
        ) {
            val data = Data.Builder()
                .putString(DATA_DOWNLOADABLE, gson.toJson(downloadable))
                .putString(DATA_DIALOG_RESULT, gson.toJson(dialogResult))
                .build()

            enqueue(context, data)
        }
    }
}
