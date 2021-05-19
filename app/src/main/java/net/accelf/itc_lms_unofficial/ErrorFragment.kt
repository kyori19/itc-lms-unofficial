package net.accelf.itc_lms_unofficial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import net.accelf.itc_lms_unofficial.ui.Icon
import net.accelf.itc_lms_unofficial.ui.NormalText
import net.accelf.itc_lms_unofficial.ui.Values
import net.accelf.itc_lms_unofficial.ui.compose

private const val ARG_ERR = "arg_err"

class ErrorFragment : ActionableFragment() {

    private var errText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            errText = it.getString(ARG_ERR)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return compose {
            ErrorFragmentContent(errText)
        }
    }

    @Composable
    @Preview
    private fun PreviewErrorFragmentContent() {
        ErrorFragmentContent(errText = "test")
    }

    @Composable
    private fun ErrorFragmentContent(errText: String?) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                modifier = Modifier.padding(Values.Spacing.around),
                imageVector = Icons.Default.Warning,
            )
            NormalText(
                modifier = Modifier.padding(Values.Spacing.around),
                text = errText ?: stringResource(id = R.string.err_default),
            )
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(errText: String? = null): ErrorFragment {
            return ErrorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ERR, errText)
                }
            }
        }
    }
}
