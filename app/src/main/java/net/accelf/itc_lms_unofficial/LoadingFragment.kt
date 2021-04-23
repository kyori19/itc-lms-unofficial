package net.accelf.itc_lms_unofficial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import net.accelf.itc_lms_unofficial.ui.NormalText
import net.accelf.itc_lms_unofficial.ui.Values
import net.accelf.itc_lms_unofficial.util.valueOf

class LoadingFragment : ActionableFragment(5000L) {

    private var loadingText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            loadingText = it.getString(ARG_LOADING_TEXT)
            actionType = valueOf(it.getString(ARG_ACTION_TYPE)) ?: ActionType.BACK_TO_MAIN
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                LoadingFragmentContent(loadingText)
            }
        }
    }

    @Composable
    @Preview
    private fun PreviewLoadingFragmentContent() {
        LoadingFragmentContent()
    }

    @Composable
    private fun LoadingFragmentContent(loadingText: String? = null) {
        MaterialTheme(colors = Values.Colors.theme) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator(color = MaterialTheme.colors.secondary)
                NormalText(text = loadingText ?: stringResource(id = R.string.loading_default))
            }
        }
    }

    companion object {
        private const val ARG_LOADING_TEXT = "loading_text"
        private const val ARG_ACTION_TYPE = "action_type"

        @JvmStatic
        fun newInstance(
            loadingText: String? = null,
            actionType: ActionType = ActionType.BACK_TO_MAIN,
        ): LoadingFragment {
            return LoadingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_LOADING_TEXT, loadingText)
                    putString(ARG_ACTION_TYPE, actionType.name)
                }
            }
        }
    }
}
