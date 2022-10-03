package net.accelf.itc_lms_unofficial.login

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.ui.*
import net.accelf.itc_lms_unofficial.util.useState

@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun LoginForm(
    onButtonClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    buttonLabel: @Composable () -> Unit,
) {
    var username by useState("")
    var password by useState("")

    Column(
        modifier = modifier,
    ) {
        TextField(
            modifier = Modifier
                .autofill(
                    autofillTypes = listOf(AutofillType.EmailAddress),
                    onFill = { username = it },
                )
                .fillMaxWidth()
                .padding(Values.Spacing.around),
            label = { Text(text = stringResource(id = R.string.login_hint_user_name)) },
            value = username,
            onValueChange = { username = it },
            textStyle = TextStyle(color = MaterialTheme.colors.onSurface),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
        )
        PasswordField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Values.Spacing.around),
            value = password,
            setValue = { password = it },
            label = { Text(text = stringResource(id = R.string.input_hint_password)) },
            textStyle = TextStyle(color = MaterialTheme.colors.onSurface),
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Values.Spacing.around),
            onClick = { onButtonClick(username, password) },
            enabled = username.isNotBlank() && password.isNotBlank(),
        ) {
            buttonLabel()
        }
    }
}

@Composable
@Preview
private fun PreviewLoginForm() {
    Root {
        LoginForm(
            onButtonClick = { _, _ -> },
        ) {
            Text("Login")
        }
    }
}
