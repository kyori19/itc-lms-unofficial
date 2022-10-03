package net.accelf.itc_lms_unofficial.login

import android.os.Handler
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.*
import kotlinx.coroutines.android.asCoroutineDispatcher
import net.accelf.itc_lms_unofficial.network.lmsHostUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LoginHelper(
    private val webView: WebView,
) : WebViewClient()  {

    private val uiScope = CoroutineScope(Handler(webView.webViewLooper).asCoroutineDispatcher())

    private var onLoaded: (suspend () -> Unit)? = null
        set(value) {
            if (field != null && value != null) {
                error("onLoaded callback already registered.")
            }
            field = value
        }

    init {
        webView.apply {
            settings.javaScriptEnabled = true
            webViewClient = this@LoginHelper
        }
        CookieManager.getInstance().apply {
            removeAllCookies(null)
            flush()
        }
    }

    /**
     * Public API
     */

    suspend fun prepare() = suspendCancellableCoroutine<Unit> {
        it.invokeOnCancellation {
            webView.stopLoading()
            onLoaded = null
        }

        onLoaded = {
            if (isLoginFormLoaded()) {
                it.resume(Unit)
                onLoaded = null
            }
        }

        webView.loadUrl(loginUrl.toString())
    }

    suspend fun login(username: String, password: String) = suspendCancellableCoroutine<Set<String>> {
        it.invokeOnCancellation {
            webView.stopLoading()
            onLoaded = null
        }

        onLoaded = {
            if (isLoggedIn()) {
                it.resume(getCredentials())
                onLoaded = null
            } else if (isMFAFormLoaded()) {
                it.resumeWithException(MFARequiredException)
                onLoaded = null
            } else if (isLoginFormLoaded()) {
                it.resumeWithException(InvalidCredentialsException)
                onLoaded = null
            }
        }

        if (username.isBlank() || password.isBlank()) {
            it.resumeWithException(InvalidCredentialsException)
        }

        CoroutineScope(Dispatchers.Main).launch {
            if (!isLoginFormLoaded()) {
                error("Login form not loaded")
            }

            fillForm("userNameInput", username)
            fillForm("passwordInput", password)
            clickButton("submitButton")
        }
    }

    suspend fun mfa(code: String) = suspendCancellableCoroutine<Set<String>> {
        it.invokeOnCancellation {
            webView.stopLoading()
            onLoaded = null
        }

        onLoaded = {
            if (isLoggedIn()) {
                it.resume(getCredentials())
                onLoaded = null
            } else if (isMFAFormLoaded()) {
                it.resumeWithException(InvalidCredentialsException)
                onLoaded = null
            }
        }

        uiScope.launch {
            if (!isMFAFormLoaded()) {
                error("MFA form not loaded")
            }

            fillForm("verificationCodeInput", code)
            clickButton("signInButton")
        }
    }

    /**
     * WebViewClient
     */

    override fun onPageFinished(view: WebView?, url: String?) {
        Log.d("LoginHelper", "onPageFinished: $url")
        CoroutineScope(Dispatchers.Main).launch {
            onLoaded?.invoke()
        }
    }

    /**
     * JavaScript Helpers
     */

    private suspend fun isLoginFormLoaded(): Boolean =
        webView.url?.toHttpUrl()?.host == "sts.adm.u-tokyo.ac.jp" &&
                fieldExists("userNameInput") && fieldExists("passwordInput")

    private suspend fun isMFAFormLoaded(): Boolean =
        webView.url?.toHttpUrl()?.host == "sts.adm.u-tokyo.ac.jp" &&
                fieldExists("verificationCodeInput")

    private fun isLoggedIn(): Boolean {
        val url = webView.url?.toHttpUrl()
        return url?.host == lmsHostUrl.host && url.pathSegments == listOf("lms", "timetable")
    }

    private fun getCredentials(): Set<String> =
        CookieManager.getInstance().getCookie(webView.url).split(';').toSet()

    private fun encodeField(value: String): String = value.replace("'", "\\'")

    private suspend fun fieldExists(id: String): Boolean {
        val script = """
            !!document.getElementById('${encodeField(id)}')
        """.trimIndent()
        return executeScript(script) == "true"
    }

    private suspend fun fillForm(id: String, value: String) {
        val script = """
            document.getElementById('${encodeField(id)}').value = '${encodeField(value)}';
        """.trimIndent()
        executeScript(script)
    }

    private suspend fun clickButton(id: String) {
        val script = """
                document.getElementById('${encodeField(id)}').click()
            """.trimIndent()
        executeScript(script)
    }

    private suspend fun executeScript(script: String): String = suspendCoroutine {
        CoroutineScope(Dispatchers.Main).launch {
            webView.evaluateJavascript(script) { res ->
                it.resume(res)
            }
        }
    }

    object MFARequiredException : Exception("MFA required")

    object InvalidCredentialsException : Exception("Invalid credentials")

    companion object {
        val loginUrl = lmsHostUrl.newBuilder()
            .addPathSegments("saml/login")
            .addQueryParameter("disco", "true")
            .build()
    }
}
