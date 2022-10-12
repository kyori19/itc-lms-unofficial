package net.accelf.itc_lms_unofficial.login

import android.os.Bundle
import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.accelf.itc_lms_unofficial.BaseActivity
import net.accelf.itc_lms_unofficial.LoadingPage
import net.accelf.itc_lms_unofficial.Prefs
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.di.SavedCookieJar
import net.accelf.itc_lms_unofficial.network.ProxyInterface
import net.accelf.itc_lms_unofficial.ui.NormalText
import net.accelf.itc_lms_unofficial.ui.Root
import net.accelf.itc_lms_unofficial.ui.Values
import net.accelf.itc_lms_unofficial.util.*
import okhttp3.HttpUrl
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity(false), BaseActivity.ProvidesUrl {

    @Inject
    lateinit var cookieJar: SavedCookieJar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.content.addView(
            ComposeView(this).apply {
                setContent {
                    val helper = remember { LoginHelper(WebView(this@LoginActivity)) }
                    var state by useState(State.NOT_INITIALIZED)

                    Root {
                        Content(
                            state = state,
                            initialLoad = {
                                helper.prepare()
                                state = State.LOGIN_FORM
                            },
                            login = { username, password ->
                                if ("Google" in username) {
                                    special()
                                    return@Content
                                }

                                state = State.LOADING
                                runCatching { helper.login(username, password) }
                                    .onSuccess {
                                        onLogin(it)
                                    }
                                    .onFailure {
                                        state = when (it) {
                                            LoginHelper.MFARequiredException -> State.MFA_FORM
                                            LoginHelper.InvalidCredentialsException -> State.LOGIN_FORM
                                            else -> throw it
                                        }
                                    }
                            },
                            mfa = { code ->
                                state = State.LOADING
                                runCatching { helper.mfa(code) }
                                    .onSuccess {
                                        onLogin(it)
                                    }
                                    .onFailure {
                                        state = when (it) {
                                            LoginHelper.InvalidCredentialsException -> State.MFA_FORM
                                            else -> throw it
                                        }
                                    }
                            }
                        )
                    }
                }
            }
        )
    }

    private fun special() {
        (lms as ProxyInterface).__special__dummy__()
        restartApp()
    }

    private fun onLogin(credentials: Set<String>) {
        defaultSharedPreference.edit()
            .putStringSet(Prefs.Keys.COOKIE, credentials)
            .apply()
        cookieJar.loadCookies()

        lms.getTimeTable()
            .withResponse(this) {
                cancelNotificationsWhichShouldBeCanceledAfterLogin()

                restartApp()
            }
    }

    override fun url(): HttpUrl = LoginHelper.loginUrl
}

private enum class State {
    NOT_INITIALIZED,
    LOADING,
    LOGIN_FORM,
    MFA_FORM,
    ;
}

@Composable
private fun Content(
    state: State,
    initialLoad: suspend () -> Unit,
    login: suspend (String, String) -> Unit,
    mfa: suspend (String) -> Unit,
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(state) {
        if (state == State.NOT_INITIALIZED) {
            initialLoad()
        }
    }

    Column {
        when (state) {
            State.NOT_INITIALIZED,
            State.LOADING,
            -> LoadingPage(
                loadingText = if (state == State.NOT_INITIALIZED) {
                    stringResource(id = R.string.loading_initialize_login)
                } else {
                    stringResource(id = R.string.loading_login)
                },
                modifier = Modifier.fillMaxSize(),
            )
            State.LOGIN_FORM -> Column {
                NormalText(
                    text = stringResource(id = R.string.login_instruction),
                    modifier = Modifier.padding(Values.Spacing.around),
                )
                LoginForm(
                    onButtonClick = { username, password ->
                        scope.launch {
                            login(username, password)
                        }
                    },
                ) {
                    Text(text = stringResource(id = R.string.button_login))
                }
            }
            State.MFA_FORM -> MFAForm(
                onButtonClick = { code ->
                    scope.launch {
                        mfa(code)
                    }
                },
            ) {
                Text(text = stringResource(id = R.string.button_login))
            }
        }
    }
}

private class PreviewContentParameterProvider : PreviewParameterProvider<State> {
    override val values: Sequence<State>
        get() = State.values().asSequence()
}

@Composable
@Preview(showBackground = true, heightDp = 300)
private fun PreviewContent(
    @PreviewParameter(PreviewContentParameterProvider::class) state: State,
) {
    Root {
        Content(
            state = state,
            initialLoad = {},
            login = { _, _ -> },
            mfa = {},
        )
    }
}
