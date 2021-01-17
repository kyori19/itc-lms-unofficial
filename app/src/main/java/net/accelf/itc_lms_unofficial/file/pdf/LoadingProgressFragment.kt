package net.accelf.itc_lms_unofficial.file.pdf

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import net.accelf.itc_lms_unofficial.BaseFragment
import net.accelf.itc_lms_unofficial.databinding.FragmentLoadingProgressBinding
import net.accelf.itc_lms_unofficial.util.setProgressBar

class LoadingProgressFragment :
    BaseFragment<FragmentLoadingProgressBinding>(FragmentLoadingProgressBinding::class.java) {

    private val viewModel by activityViewModels<PdfViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.pdfFile.setProgressBar(viewLifecycleOwner, binding.progressDownload)
    }

    companion object {

        fun newInstance(): LoadingProgressFragment {
            return LoadingProgressFragment()
        }
    }
}
