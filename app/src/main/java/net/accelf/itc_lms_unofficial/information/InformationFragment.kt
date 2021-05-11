package net.accelf.itc_lms_unofficial.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import dagger.hilt.android.AndroidEntryPoint
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.di.CustomLinkMovementMethod
import net.accelf.itc_lms_unofficial.models.Information
import net.accelf.itc_lms_unofficial.ui.NormalText
import net.accelf.itc_lms_unofficial.ui.Values
import net.accelf.itc_lms_unofficial.util.Request
import net.accelf.itc_lms_unofficial.util.Success
import net.accelf.itc_lms_unofficial.util.fromHtml
import net.accelf.itc_lms_unofficial.util.mutableLiveDataOf
import javax.inject.Inject

@AndroidEntryPoint
class InformationFragment : Fragment() {

    @Inject
    lateinit var linkMovementMethod: CustomLinkMovementMethod

    private val viewModel by activityViewModels<InformationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                InformationFragmentContent(viewModel.information, linkMovementMethod)
            }
        }
    }

    @Composable
    @Preview
    private fun PreviewInformationFragmentContent() {
        InformationFragmentContent(mutableLiveDataOf(Success(Information(
            "<ul>" +
                    "<li>information content</li>" +
                    "</ul>"
        ))), CustomLinkMovementMethod())
    }

    @Composable
    private fun InformationFragmentContent(
        liveInformation: LiveData<Request<Information>>,
        linkMovementMethod: CustomLinkMovementMethod,
    ) {
        val information by liveInformation.observeAsState()

        MaterialTheme(Values.Colors.theme) {
            Card(
                modifier = Modifier.padding(Values.Spacing.around),
                backgroundColor = Values.Colors.Gray.surface,
            ) {
                Column {
                    NormalText(
                        text = stringResource(id = R.string.title_information),
                        modifier = Modifier.padding(Values.Spacing.around),
                        fontSize = Values.Text.large,
                    )
                    AndroidView(
                        { TextView(it) },
                        modifier = Modifier.padding(Values.Spacing.around),
                    ) {
                        it.movementMethod = linkMovementMethod
                        if (information is Success) {
                            it.text = (information as Success).data.text.fromHtml()
                        }
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): InformationFragment {
            return InformationFragment()
        }
    }
}
