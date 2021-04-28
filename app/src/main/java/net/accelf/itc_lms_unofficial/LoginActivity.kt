package net.accelf.itc_lms_unofficial

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import net.accelf.itc_lms_unofficial.di.SavedCookieJar
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.util.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

@AndroidEntryPoint
class LoginActivity : BaseActivity(false) {

    @Inject
    lateinit var lms: LMS

    @Inject
    lateinit var cookieJar: SavedCookieJar

    private val waitForScripting = ReentrantLock()
    private val loginRequestFragment by lazy {
        LoginRequestFragment.newInstance()
    }
    private val webView by lazy {
        WebView(this).apply {
            @SuppressLint("SetJavaScriptEnabled")
            settings.javaScriptEnabled = true
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    if (url?.toHttpUrl()?.host == "sts.adm.u-tokyo.ac.jp") {
                        if (waitForScripting.isLocked) {
                            waitForScripting.unlock()
                            return
                        }
                    }
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    replaceErrorFragment(getString(R.string.err_page_not_loaded))
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    if (request?.url?.path.equals("/lms/timetable")) {
                        finishLogin(
                            CookieManager.getInstance().getCookie(request?.url.toString())
                        )
                        return true
                    }
                    return super.shouldOverrideUrlLoading(view, request)
                }
            }
            loadUrl("https://itc-lms.ecc.u-tokyo.ac.jp/saml/login?disco=true")
        }
    }

    private val interceptedDialog by lazy {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_title_bug_notify)
            .setMessage(R.string.dialog_message_bug_notify)
            .setPositiveButton(R.string.button_dialog_close) { dialog, _ ->
                dialog.dismiss()
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.getBooleanExtra(EXTRA_REQUEST_INTERCEPTED, false)) {
            interceptedDialog.show()
        }

        waitForScripting.lock()
        replaceFragment(loginRequestFragment)

        CookieManager.getInstance().apply {
            removeAllCookies(null)
            flush()
        }
    }

    fun onLoginClick(userName: String, password: String) {
        replaceFragment(
            LoadingFragment.newInstance(
                getString(R.string.loading_check_account),
                ActionableFragment.ActionType.RETRY_LOGIN
            )
        )
        webView // Initialize lazy val
        thread {
            waitForScripting.withLock {
                runOnUiThread {
                    webView.apply {
                        evaluateJavascript("document.getElementById('userNameInput').value = '$userName'") {
                            javascriptCallback(
                                it
                            )
                        }
                        evaluateJavascript("document.getElementById('passwordInput').value = '$password'") {
                            javascriptCallback(
                                it
                            )
                        }
                        evaluateJavascript("document.getElementById('submitButton').click()", null)
                    }
                }
            }
        }
    }

    private fun javascriptCallback(result: String) {
        // Setting form value returns inputted value.
        // When clicking, it returns null so don't set this callback.
        if (result == "null") {
            replaceErrorFragment(getString(R.string.err_page_not_loaded))
        }
    }

    private fun finishLogin(cookie: String) {
        defaultSharedPreference.edit()
            .putStringSet(Prefs.Keys.COOKIE, cookie.split(";").toSet())
            .apply()
        cookieJar.loadCookies()

        lms.getTimeTable()
            .withResponse(this) {
                cancelNotificationsWhichShouldBeCanceledAfterLogin()

                restartApp()
            }
    }

    fun retryLogin() {
        replaceFragment(loginRequestFragment)
    }

    companion object {
        private const val EXTRA_REQUEST_INTERCEPTED = "request_intercepted"

        fun intent(context: Context, intercepted: Boolean = false): Intent {
            return Intent(context, LoginActivity::class.java)
                .apply {
                    if (intercepted) {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        putExtra(EXTRA_REQUEST_INTERCEPTED, true)
                    }
                }
        }
    }
}
