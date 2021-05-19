package net.accelf.itc_lms_unofficial.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import dagger.hilt.android.AndroidEntryPoint
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.di.CustomLinkMovementMethod
import net.accelf.itc_lms_unofficial.models.Information
import net.accelf.itc_lms_unofficial.ui.SpannedText
import net.accelf.itc_lms_unofficial.ui.TitledCard
import net.accelf.itc_lms_unofficial.ui.Values
import net.accelf.itc_lms_unofficial.ui.compose
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
        return compose {
            InformationFragmentContent(viewModel.information, linkMovementMethod)
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

        if (information is Success) {
            TitledCard(
                title = stringResource(id = R.string.title_information),
                modifier = Modifier.padding(Values.Spacing.around),
            ) {
                SpannedText(
                    text = (information as Success).data.text.fromHtml(),
                    modifier = Modifier.padding(Values.Spacing.around),
                ) {
                    movementMethod = linkMovementMethod
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
