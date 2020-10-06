package net.accelf.itc_lms_unofficial.file.pdf

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_loading_progress.*
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.util.setProgressBar

class LoadingProgressFragment : Fragment(R.layout.fragment_loading_progress) {

    private val viewModel by activityViewModels<PdfViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.pdfFile.setProgressBar(viewLifecycleOwner, progressDownload)
    }

    companion object {

        fun newInstance(): LoadingProgressFragment {
            return LoadingProgressFragment()
        }
    }
}
