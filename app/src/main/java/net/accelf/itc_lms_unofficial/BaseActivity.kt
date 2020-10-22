package net.accelf.itc_lms_unofficial

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewTreeLifecycleOwner
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.aboutlibraries.LibsBuilder
import kotlinx.android.synthetic.main.activity_base.*
import net.accelf.itc_lms_unofficial.settings.PreferenceActivity
import net.accelf.itc_lms_unofficial.task.PullUpdatesWorker
import net.accelf.itc_lms_unofficial.task.TaskManagerActivity
import net.accelf.itc_lms_unofficial.util.startActivity
import okhttp3.HttpUrl

// FIXME: The base class of the @AndroidEntryPoint, contains a constructor with default parameters.
//  This is currently not supported by the Hilt Gradle plugin.
open class BaseActivity(val swipeRefreshEnabled: Boolean) :
    AppCompatActivity(R.layout.activity_base) {

    private val customTabsIntent by lazy {
        CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .build()
    }

    private val copiedSnackbar by lazy {
        Snackbar.make(content, R.string.snackbar_copied, Snackbar.LENGTH_SHORT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(toolbar)

        swipeRefresh.isEnabled = swipeRefreshEnabled

        ViewTreeLifecycleOwner.set(window.decorView, this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannels(
                    listOf(
                        NotificationChannel(
                            Notifications.Channels.LMS_UPDATES,
                            getString(R.string.notify_name_lms_updates),
                            NotificationManager.IMPORTANCE_HIGH
                        ).apply {
                            description = getString(R.string.notify_desc_lms_updates)
                        },
                        NotificationChannel(
                            Notifications.Channels.ERRORS,
                            getString(R.string.notify_name_errors),
                            NotificationManager.IMPORTANCE_DEFAULT
                        ).apply {
                            description = getString(R.string.notify_desc_errors)
                        },
                        NotificationChannel(
                            Notifications.Channels.DOWNLOADS,
                            getString(R.string.notify_name_downloads),
                            NotificationManager.IMPORTANCE_LOW
                        ).apply {
                            description = getString(R.string.notify_desc_downloads)
                        }
                    )
                )
        }

        PullUpdatesWorker.enqueue(applicationContext)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)

        listOf(R.id.actionOpenInBrowser, R.id.actionCopyUrl, R.id.actionShareUrl).forEach {
            menu?.findItem(it)?.isVisible = this is ProvidesUrl
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionSettings -> {
                startActivity<PreferenceActivity>()
                true
            }
            R.id.actionOpenTaskManager -> {
                startActivity<TaskManagerActivity>()
                true
            }
            R.id.actionOpenInBrowser -> {
                customTabsIntent.launchUrl(this, Uri.parse((this as ProvidesUrl).url().toString()))
                true
            }
            R.id.actionCopyUrl -> {
                (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                    .setPrimaryClip(
                        ClipData.newUri(
                            contentResolver,
                            "URI",
                            Uri.parse((this as ProvidesUrl).url().toString())
                        )
                    )
                copiedSnackbar.show()
                true
            }
            R.id.actionShareUrl -> {
                startActivity(
                    createChooser(
                        Intent().apply {
                            action = ACTION_SEND
                            putExtra(
                                EXTRA_TEXT,
                                (this@BaseActivity as ProvidesUrl).url().toString()
                            )
                            type = "text/plain"
                        },
                        null
                    )
                )
                true
            }
            R.id.openAbout -> {
                LibsBuilder().apply {
                    showLicense = true
                    aboutAppName = getString(R.string.app_name)
                    activityTitle = getString(R.string.title_about)

                    withLibraryModification(libraries)
                }.start(this)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    interface ProvidesUrl {
        fun url(): HttpUrl
    }
}
