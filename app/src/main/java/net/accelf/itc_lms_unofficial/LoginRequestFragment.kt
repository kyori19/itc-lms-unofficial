package net.accelf.itc_lms_unofficial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import net.accelf.itc_lms_unofficial.ui.NormalText
import net.accelf.itc_lms_unofficial.ui.PasswordField
import net.accelf.itc_lms_unofficial.ui.Values
import net.accelf.itc_lms_unofficial.ui.autofill

class LoginRequestFragment : Fragment() {

    private val mutableUserName = mutableStateOf("")
    private val mutablePassword = mutableStateOf("")

    @ExperimentalComposeUiApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                LoginRequestFragmentContent()
            }
        }
    }

    @ExperimentalComposeUiApi
    @Composable
    @Preview
    private fun LoginRequestFragmentContent() {
        val userName by remember { mutableUserName }
        val password by remember { mutablePassword }

        MaterialTheme(colors = Values.Colors.theme) {
            Column {
                NormalText(
                    modifier = Modifier.padding(Values.Spacing.around),
                    text = stringResource(id = R.string.login_instruction),
                    fontSize = Values.Text.large,
                )
                TextField(
                    modifier = Modifier
                        .autofill(
                            autofillTypes = listOf(AutofillType.EmailAddress),
                            onFill = { mutableUserName.value = it },
                        )
                        .fillMaxWidth()
                        .padding(Values.Spacing.around),
                    label = { Text(text = stringResource(id = R.string.login_hint_user_name)) },
                    value = userName,
                    onValueChange = { mutableUserName.value = it },
                    textStyle = TextStyle(color = MaterialTheme.colors.onSurface),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                )
                PasswordField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Values.Spacing.around),
                    mutableValue = mutablePassword,
                    label = { Text(text = stringResource(id = R.string.input_hint_password)) },
                    textStyle = TextStyle(color = MaterialTheme.colors.onSurface),
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Values.Spacing.around),
                    onClick = { (activity as LoginActivity).onLoginClick(userName, password) },
                    enabled = userName.isNotBlank() && password.isNotBlank(),
                ) {
                    Text(text = stringResource(id = R.string.button_login))
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): LoginRequestFragment {
            return LoginRequestFragment()
        }
    }
}
