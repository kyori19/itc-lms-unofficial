package net.accelf.itc_lms_unofficial

import android.os.Bundle
import android.view.View
import net.accelf.itc_lms_unofficial.databinding.FragmentLoadingBinding

private const val ARG_LOADING_TEXT = "loading_text"

class LoadingFragment(login: Boolean) : ActionableFragment(
    R.layout.fragment_loading,
    if (login) {
        ActionableFragment.Companion.ActionType.RETRY_LOGIN
    } else {
        ActionableFragment.Companion.ActionType.BACK_TO_MAIN
    },
    5000L
) {

    private var _binding: FragmentLoadingBinding? = null
    private val binding get() = _binding!!

    private var loadingText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            loadingText = it.getString(ARG_LOADING_TEXT)
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
        @JvmStatic
        fun newInstance(
            loadingText: String? = null,
            login: Boolean = false,
        ): LoadingFragment {
            return LoadingFragment(login).apply {
                arguments = Bundle().apply {
                    putString(ARG_LOADING_TEXT, loadingText)
                }
            }
        }
    }
}
