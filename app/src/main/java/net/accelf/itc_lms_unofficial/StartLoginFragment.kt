package net.accelf.itc_lms_unofficial

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_start_login.*

class StartLoginFragment : Fragment(R.layout.fragment_start_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonStartLogin.setOnClickListener {
            startActivity(LoginActivity.intent(requireContext()))
            activity?.finish()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): StartLoginFragment {
            return StartLoginFragment()
        }
    }
}
