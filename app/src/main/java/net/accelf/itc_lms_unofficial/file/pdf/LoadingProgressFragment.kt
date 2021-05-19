package net.accelf.itc_lms_unofficial.file.pdf

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import net.accelf.itc_lms_unofficial.ui.Values
import net.accelf.itc_lms_unofficial.ui.compose
import net.accelf.itc_lms_unofficial.util.Progress
import net.accelf.itc_lms_unofficial.util.Request
import net.accelf.itc_lms_unofficial.util.mutableLiveDataOf

class LoadingProgressFragment : Fragment() {

    private val viewModel by activityViewModels<PdfViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return compose {
            LoadingProgressFragmentContent(viewModel.pdfFile)
        }
    }

    @Composable
    @Preview
    private fun PreviewLoadingProgressFragmentContent() {
        LoadingProgressFragmentContent(mutableLiveDataOf(Progress(0.5f)))
    }

    @Composable
    private fun LoadingProgressFragmentContent(livePdfFile: LiveData<Request<ByteArray>>) {
        val pdfFile by livePdfFile.observeAsState()

        Box(
            contentAlignment = Alignment.Center,
        ) {
            val outerStrokeWidth = 10.dp
            val outerCircleWidth = 108.dp
            val innerDiff = 2.dp

            CircularProgressIndicator(
                modifier = Modifier
                    .padding(Values.Spacing.around + innerDiff / 2)
                    .size(outerCircleWidth - innerDiff),
                progress = 1f,
                color = Values.Colors.Gray.darken,
                strokeWidth = outerStrokeWidth - innerDiff,
            )
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(Values.Spacing.around)
                    .size(outerCircleWidth),
                progress = if (pdfFile is Progress) (pdfFile as Progress<ByteArray>).progress else 0f,
                color = MaterialTheme.colors.secondary,
                strokeWidth = outerStrokeWidth,
            )
        }
    }

    companion object {

        fun newInstance(): LoadingProgressFragment {
            return LoadingProgressFragment()
        }
    }
}
