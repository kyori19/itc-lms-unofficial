package net.accelf.itc_lms_unofficial

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewTreeLifecycleOwner
import kotlinx.android.synthetic.main.activity_base.*
import net.accelf.itc_lms_unofficial.task.PullUpdatesWorker
import net.accelf.itc_lms_unofficial.task.TaskManagerActivity

const val CHANNEL_ID_LMS_UPDATES = "lms_updates"

open class BaseActivity : AppCompatActivity(R.layout.activity_base) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(toolbar)

        ViewTreeLifecycleOwner.set(window.decorView, this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(
                    NotificationChannel(
                        CHANNEL_ID_LMS_UPDATES,
                        getString(R.string.notify_name_lms_updates),
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply {
                        description = getString(R.string.notify_desc_lms_updates)
                    }
                )
        }

        PullUpdatesWorker.enqueue(applicationContext)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionOpenTaskManager -> {
                startActivity(TaskManagerActivity.intent(this))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}
