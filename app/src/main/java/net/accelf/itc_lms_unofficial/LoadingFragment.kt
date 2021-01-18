package net.accelf.itc_lms_unofficial

import android.os.Bundle
import android.view.View
import net.accelf.itc_lms_unofficial.databinding.FragmentLoadingBinding

class LoadingFragment : ActionableFragment(R.layout.fragment_loading, 5000L) {

    private var _binding: FragmentLoadingBinding? = null
    private val binding get() = _binding!!

    private var loadingText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            loadingText = it.getString(ARG_LOADING_TEXT)
            actionType = ActionType.valueOf(ARG_ACTION_TYPE)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoadingBinding.bind(view)

        binding.textLoading.text = loadingText ?: getString(R.string.loading_default)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
