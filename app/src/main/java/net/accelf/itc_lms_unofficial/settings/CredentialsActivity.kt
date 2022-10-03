package net.accelf.itc_lms_unofficial.settings

import android.os.Bundle
import androidx.activity.addCallback
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.core.content.edit
import dagger.hilt.android.AndroidEntryPoint
import net.accelf.itc_lms_unofficial.BaseActivity
import net.accelf.itc_lms_unofficial.Prefs
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.di.EncryptedDataStore
import net.accelf.itc_lms_unofficial.login.LoginForm
import net.accelf.itc_lms_unofficial.login.RegisterMFA
import net.accelf.itc_lms_unofficial.ui.NormalText
import net.accelf.itc_lms_unofficial.ui.Root
import net.accelf.itc_lms_unofficial.ui.Values
import net.accelf.itc_lms_unofficial.util.defaultSharedPreference
import net.accelf.itc_lms_unofficial.util.startActivity
import net.accelf.itc_lms_unofficial.util.useState
import javax.inject.Inject

@AndroidEntryPoint
class CredentialsActivity : BaseActivity(false) {

    @Inject
    lateinit var encryptedDataStore: EncryptedDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.content.addView(
            ComposeView(this).apply {
                setContent {
                    Root {
                        var state by useState(State.LOGIN)

                        Content(
                            state = state,
                            login = { username, password ->
                                encryptedDataStore.apply {
                                    putString(Prefs.Keys.LOGIN_USERNAME, username)
                                    putString(Prefs.Keys.LOGIN_PASSWORD, password)
                                }
                                state = State.MFA
                            },
                            mfa = {
                                encryptedDataStore.putString(Prefs.Keys.MFA_SECRET, it)
                            },
                            done = ::complete,
                        )
                    }
                }
            }
        )

        onBackPressedDispatcher.addCallback {
            removeCredentials()
            finish()
            startActivity<PreferenceActivity>()
        }
    }

    private fun removeCredentials() {
        encryptedDataStore.apply {
            listOf(
                Prefs.Keys.LOGIN_USERNAME,
                Prefs.Keys.LOGIN_PASSWORD,
                Prefs.Keys.MFA_SECRET,
            ).forEach {
                putString(it, "")
            }
        }
    }

    private fun complete() {
        defaultSharedPreference.edit {
            putBoolean(Prefs.Keys.AUTOMATE_LOGIN, true)
        }
        finish()
        startActivity<PreferenceActivity>()
    }
}

private enum class State {
    LOGIN,
    MFA,
    ;
}

@Composable
private fun Content(
    state: State,
    login: (String, String) -> Unit,
    mfa: (String) -> Unit,
    done: () -> Unit,
) {
    when (state) {
        State.LOGIN -> LoginForm(
            onButtonClick = login,
        ) {
            Text(text = stringResource(id = R.string.button_save))
        }
        State.MFA -> Column {
            RegisterMFA(
                onButtonClick = {
                    mfa(it)
                    done()
                },
            ) {
                Text(text = stringResource(id = R.string.button_save))
            }

            NormalText(
                text = stringResource(id = R.string.without_mfa),
                modifier = Modifier.padding(Values.Spacing.around),
            )
            OutlinedButton(
                onClick = done,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Values.Spacing.around),
            ) {
                Text(text = stringResource(id = R.string.button_without_mfa))
            }
        }
    }
}

private class PreviewStateProvider : PreviewParameterProvider<State> {
    override val values: Sequence<State>
        get() = State.values().asSequence()
}

@Composable
@Preview
private fun PreviewContent(
    @PreviewParameter(PreviewStateProvider::class) state: State,
) {
    Root {
        Content(
            state = state,
            login = { _, _ -> },
            mfa = {},
            done = {},
        )
    }
}
