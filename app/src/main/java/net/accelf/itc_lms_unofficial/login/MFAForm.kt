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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.ui.NormalText
import net.accelf.itc_lms_unofficial.ui.Root
import net.accelf.itc_lms_unofficial.ui.Values
import net.accelf.itc_lms_unofficial.util.useState

@Composable
fun MFAForm(
    onButtonClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    buttonLabel: @Composable () -> Unit,
) {
    var code by useState("")

    Column(
        modifier = modifier,
    ) {
        NormalText(
            text = stringResource(id = R.string.mfa_instruction),
            modifier = Modifier.padding(Values.Spacing.around),
        )
        TextField(
            value = code,
            onValueChange = { code = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(Values.Spacing.around),
            label = { Text(text = stringResource(id = R.string.input_hint_verification_code)) },
            textStyle = TextStyle(color = MaterialTheme.colors.onSurface),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Values.Spacing.around),
            onClick = { onButtonClick(code) },
            enabled = code.isNotBlank(),
        ) {
            buttonLabel()
        }
    }
}

@Composable
@Preview
private fun PreviewMFAForm() {
    Root {
        MFAForm(
            onButtonClick = {},
        ) {
            Text("Login")
        }
    }
}
