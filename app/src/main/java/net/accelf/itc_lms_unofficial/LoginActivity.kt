package net.accelf.itc_lms_unofficial

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import net.accelf.itc_lms_unofficial.di.SavedCookieJar
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.util.call
import net.accelf.itc_lms_unofficial.util.defaultSharedPreference
import net.accelf.itc_lms_unofficial.util.replaceErrorFragment
import net.accelf.itc_lms_unofficial.util.replaceFragment
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

const val PREF_COOKIE = "cookie"

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

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
    private var clicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                true
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
                clicked = true
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
            .putStringSet(PREF_COOKIE, cookie.split(";").toSet())
            .apply()
        cookieJar.loadCookies()

        lms.getLog()
            .call(this)
            .subscribe({
                startActivity(MainActivity.intent(this))
                finish()
            }, { throwable ->
                replaceErrorFragment(throwable)
            })
    }

    fun retryLogin() {
        replaceFragment(loginRequestFragment)
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
}
