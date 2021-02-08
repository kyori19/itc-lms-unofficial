package net.accelf.itc_lms_unofficial.information

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import net.accelf.itc_lms_unofficial.BaseFragment
import net.accelf.itc_lms_unofficial.databinding.FragmentInformationBinding
import net.accelf.itc_lms_unofficial.di.CustomLinkMovementMethod
import net.accelf.itc_lms_unofficial.util.fromHtml
import net.accelf.itc_lms_unofficial.util.onSuccess
import javax.inject.Inject

@AndroidEntryPoint
class InformationFragment :
    BaseFragment<FragmentInformationBinding>(FragmentInformationBinding::class.java) {

    @Inject
    lateinit var linkMovementMethod: CustomLinkMovementMethod

    private val viewModel by activityViewModels<InformationViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textInformationContent.movementMethod = linkMovementMethod

        viewModel.information.onSuccess(viewLifecycleOwner) { information ->
            binding.textInformationContent.text = information.text.fromHtml()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): InformationFragment {
            return InformationFragment()
        }
    }
}
