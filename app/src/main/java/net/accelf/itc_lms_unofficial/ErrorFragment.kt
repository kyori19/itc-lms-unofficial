package net.accelf.itc_lms_unofficial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_error.*

private const val ARG_ERR = "arg_err"

class ErrorFragment : ActionableFragment(ActionableFragment.Companion.ActionType.BACK_TO_MAIN) {
    private var errText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            errText = it.getString(ARG_ERR)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_error, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textError.text = errText ?: getString(R.string.err_default)
    }

    companion object {
        @JvmStatic
        fun newInstance(errText: String? = null): ErrorFragment {
            return ErrorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ERR, errText)
                }
            }
        }
    }
}
