package net.accelf.itc_lms_unofficial.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import dev.turingcomplete.kotlinonetimepassword.GoogleAuthenticator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.ui.NormalText
import net.accelf.itc_lms_unofficial.ui.Values
import net.accelf.itc_lms_unofficial.util.useState
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private val interval = 30.seconds.inWholeMilliseconds

@Composable
fun RegisterMFA(
    onButtonClick: (String) -> Unit,
    buttonLabel: @Composable () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val time by timer().collectAsState(System.currentTimeMillis())
    var secret by useState("")
    val generator = remember(secret) {
        GoogleAuthenticator(secret.ifEmpty { "secret" }.toByteArray())
    }
    val code = remember(generator.hashCode(), time / interval) {
        generator.generate()
    }

    Column {
        NormalText(
            text = stringResource(id = R.string.mfa_register_instruction),
            modifier = Modifier.padding(Values.Spacing.around),
        )
        Button(
            onClick = {
                uriHandler.openUri("https://mysignins.microsoft.com/security-info")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(Values.Spacing.around),
        ) {
            Text(text = "Microsoft My Sign-Ins")
        }
        TextField(
            value = secret,
            onValueChange = { secret = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(Values.Spacing.around),
            label = { Text(text = stringResource(id = R.string.input_hint_mfa_secret)) },
            textStyle = TextStyle(color = MaterialTheme.colors.onSurface),
            singleLine = true,
        )
        NormalText(
            text = code,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Values.Spacing.around),
            fontSize = Values.Text.large,
            color = MaterialTheme.colors.secondary,
            textAlign = TextAlign.Center,
        )
        LinearProgressIndicator(
            progress = 1 - ((time % interval).toFloat() / interval),
            modifier = Modifier
                .fillMaxWidth()
                .padding(Values.Spacing.around),
            color = MaterialTheme.colors.secondary,
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Values.Spacing.around),
            onClick = { onButtonClick(secret) },
            enabled = secret.isNotBlank(),
        ) {
            buttonLabel()
        }
    }
}

private fun timer(): Flow<Long> = flow {
    while (true) {
        emit(System.currentTimeMillis())
        delay(100.milliseconds)
    }
}

@Composable
@Preview
private fun PreviewRegisterMFA() {
    RegisterMFA(
        onButtonClick = {},
    ) {
        Text(text = "Save")
    }
}
