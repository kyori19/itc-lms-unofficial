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
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewTreeLifecycleOwner
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.aboutlibraries.LibsBuilder
import net.accelf.itc_lms_unofficial.databinding.ActivityBaseBinding
import net.accelf.itc_lms_unofficial.information.InformationActivity
import net.accelf.itc_lms_unofficial.models.Update
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.settings.PreferenceActivity
import net.accelf.itc_lms_unofficial.updates.EXTRA_CSRF
import net.accelf.itc_lms_unofficial.updates.EXTRA_UPDATE
import net.accelf.itc_lms_unofficial.updates.PullUpdatesWorker
import net.accelf.itc_lms_unofficial.task.TaskManagerActivity
import net.accelf.itc_lms_unofficial.updates.CancelNotificationReceiver
import net.accelf.itc_lms_unofficial.util.getSerializableExtraCompat
import net.accelf.itc_lms_unofficial.util.startActivity
import okhttp3.HttpUrl
import javax.inject.Inject

// FIXME: The base class of the @AndroidEntryPoint, contains a constructor with default parameters.
//  This is currently not supported by the Hilt Gradle plugin.
open class BaseActivity(val swipeRefreshEnabled: Boolean) : AppCompatActivity() {

    @Inject
    lateinit var lms: LMS

    lateinit var binding: ActivityBaseBinding

    private val customTabsIntent by lazy {
        CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setDefaultColorSchemeParams(
                CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .build()
            )
            .build()
    }

    private val copiedSnackbar by lazy {
        Snackbar.make(binding.content, R.string.snackbar_copied, Snackbar.LENGTH_SHORT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.swipeRefresh.isEnabled = swipeRefreshEnabled

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

        (intent.getSerializableExtraCompat<Update>(EXTRA_UPDATE))?.let { update ->
            sendBroadcast(CancelNotificationReceiver.intent(this, update, intent.getStringExtra(EXTRA_CSRF)!!))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)

        listOf(R.id.actionOpenInBrowser, R.id.actionCopyUrl, R.id.actionShareUrl).forEach {
            menu.findItem(it)?.isVisible = this is ProvidesUrl
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
            R.id.actionLmsInformation -> {
                startActivity<InformationActivity>()
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
