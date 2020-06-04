package net.accelf.itc_lms_unofficial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_loading.*

private const val ARG_LOADING_TEXT = "loading_text"

class LoadingFragment(login: Boolean) : ActionableFragment(
    if (login) {
        ActionableFragment.Companion.ActionType.RETRY_LOGIN
    } else {
        ActionableFragment.Companion.ActionType.BACK_TO_MAIN
    },
    3000L
) {

    private var loadingText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            loadingText = it.getString(ARG_LOADING_TEXT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textLoading.text = loadingText ?: getString(R.string.loading_default)
    }

    companion object {
        @JvmStatic
        fun newInstance(
            loadingText: String? = null,
            login: Boolean = false
        ): LoadingFragment {
            return LoadingFragment(login).apply {
                arguments = Bundle().apply {
                    putString(ARG_LOADING_TEXT, loadingText)
                }
            }
        }
    }
}
