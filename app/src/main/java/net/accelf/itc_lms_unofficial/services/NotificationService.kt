package net.accelf.itc_lms_unofficial.services

import android.content.Context
import android.content.Intent
import androidx.core.app.TaskStackBuilder
import androidx.lifecycle.LifecycleService
import dagger.hilt.android.AndroidEntryPoint
import net.accelf.itc_lms_unofficial.coursedetail.CourseDetailActivity
import net.accelf.itc_lms_unofficial.coursedetail.CourseDetailActivity.Companion.putCourseId
import net.accelf.itc_lms_unofficial.models.Update
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.network.lmsHostUrl
import net.accelf.itc_lms_unofficial.reportdetail.ReportDetailActivity
import net.accelf.itc_lms_unofficial.util.call
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : LifecycleService() {

    @Inject
    lateinit var lms: LMS

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.also {
            val update = it.getSerializableExtra(EXTRA_UPDATE) as Update
            val csrf = it.getStringExtra(EXTRA_CSRF)!!

            if (!it.getBooleanExtra(EXTRA_CANCEL_ONLY, false)) {
                when (update.contentType) {
                    Update.ContentType.MATERIAL -> {
                        TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(
                                CourseDetailActivity.intent(
                                    applicationContext,
                                    update.courseId,
                                    materialId = update.contentId,
                                )
                            )
                            .startActivities()
                    }
                    Update.ContentType.NOTIFY -> {
                        TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(
                                CourseDetailActivity.intent(
                                    applicationContext,
                                    update.courseId,
                                    notifyId = update.contentId,
                                )
                            )
                            .startActivities()
                    }
                    Update.ContentType.ONLINE_INFO -> {
                        TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(
                                CourseDetailActivity.intent(
                                    applicationContext,
                                    update.courseId,
                                    openDescription = true,
                                )
                            )
                            .startActivities()
                    }
                    Update.ContentType.REPORT -> {
                        TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(
                                ReportDetailActivity.intent(
                                    applicationContext,
                                    update.courseId,
                                    update.contentId,
                                )
                            )
                            .apply {
                                editIntentAt(intents.indexOfFirst { i -> i.component?.className == CourseDetailActivity::class.java.name })
                                    ?.putCourseId(update.courseId)
                            }
                            .startActivities()
                    }
                    else -> {
                        val segments = update.url.split("?")
                        val path = segments[0].substring(1)
                        val queries = (segments.getOrNull(1) ?: "").split("&")
                            .map { query -> query.split("=").let { kv -> kv[0] to kv[1] } }
                        val url = lmsHostUrl.newBuilder()
                            .addPathSegments(path)
                            .apply {
                                queries.forEach { (k, v) -> addQueryParameter(k, v) }
                            }
                            .build()
                            .toString()
                        TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(
                                CourseDetailActivity.intent(
                                    applicationContext,
                                    update.courseId,
                                    url = url,
                                )
                            )
                            .startActivities()
                    }
                }
            }

            lms.deleteUpdates(csrf, listOf(update.targetId))
                .call(this)
                .subscribe(
                    {},
                    { throwable ->
                        throwable.printStackTrace()
                    }
                )
        }
        return super.onStartCommand(intent, flags, startId)
    }

    companion object {
        private const val EXTRA_UPDATE = "update"
        private const val EXTRA_CSRF = "csrf"
        private const val EXTRA_CANCEL_ONLY = "cancel_only"

        fun intent(
            context: Context,
            update: Update,
            csrf: String,
            cancelOnly: Boolean,
        ): Intent {
            return Intent(context, NotificationService::class.java)
                .apply {
                    putExtra(EXTRA_UPDATE, update)
                    putExtra(EXTRA_CSRF, csrf)
                    putExtra(EXTRA_CANCEL_ONLY, cancelOnly)
                }
        }
    }
}
