package net.accelf.itc_lms_unofficial.ui

import android.view.View
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment

fun Fragment.compose(
    content: @Composable () -> Unit,
): View {
    return ComposeView(requireContext()).apply {
        setContent {
            Root {
                content()
            }
        }
    }
}

@Composable
fun Root(
    content: @Composable () -> Unit,
) {
    MaterialTheme(colors = Values.Theme.colors) {
        content()
    }
}
