package net.accelf.itc_lms_unofficial.updates

import android.content.Context
import androidx.core.app.TaskStackBuilder
import net.accelf.itc_lms_unofficial.coursedetail.CourseDetailActivity
import net.accelf.itc_lms_unofficial.coursedetail.CourseDetailActivity.Companion.putCourseId
import net.accelf.itc_lms_unofficial.models.Update
import net.accelf.itc_lms_unofficial.network.lmsHostUrl
import net.accelf.itc_lms_unofficial.reportdetail.ReportDetailActivity

fun Update.toPendingIntent(context: Context, csrf: String, requestCode: Int, flags: Int) =
    TaskStackBuilder.create(context)
        .apply {
            addNextIntentWithParentStack(when (contentType) {
                Update.ContentType.MATERIAL -> CourseDetailActivity.intent(
                    context,
                    courseId,
                    materialId = contentId,
                )
                Update.ContentType.NOTIFY -> CourseDetailActivity.intent(
                    context,
                    courseId,
                    notifyId = contentId,
                )
                Update.ContentType.ONLINE_INFO -> CourseDetailActivity.intent(
                    context,
                    courseId,
                    openDescription = true,
                )
                Update.ContentType.REPORT -> ReportDetailActivity.intent(
                    context,
                    courseId,
                    contentId
                )
                else -> CourseDetailActivity.intent(
                    context,
                    courseId,
                    url = lmsHostUrl.newBuilder(url)!!.build().toString(),
                )
            })

            @Suppress("NON_EXHAUSTIVE_WHEN_STATEMENT")
            when (contentType) {
                Update.ContentType.REPORT -> {
                    editIntentAt(intents.indexOfFirst { it.component?.className == CourseDetailActivity::class.java.name })
                        ?.putCourseId(courseId)
                }
            }

            editIntentAt(intentCount - 1)?.apply {
                putExtra(EXTRA_UPDATE, this@toPendingIntent)
                putExtra(EXTRA_CSRF, csrf)
            }
        }
        .getPendingIntent(requestCode, flags)

const val EXTRA_UPDATE = "update"
const val EXTRA_CSRF = "csrf"
