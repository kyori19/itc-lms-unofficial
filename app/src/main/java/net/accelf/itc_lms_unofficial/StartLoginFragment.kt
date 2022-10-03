package net.accelf.itc_lms_unofficial

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import net.accelf.itc_lms_unofficial.databinding.FragmentStartLoginBinding
import net.accelf.itc_lms_unofficial.login.LoginActivity
import net.accelf.itc_lms_unofficial.util.startActivity

class StartLoginFragment : Fragment(R.layout.fragment_start_login) {

    private var _binding: FragmentStartLoginBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStartLoginBinding.bind(view)

        binding.buttonStartLogin.setOnClickListener {
            requireContext().startActivity<LoginActivity>()
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
