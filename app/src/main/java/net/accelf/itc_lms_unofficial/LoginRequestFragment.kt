package net.accelf.itc_lms_unofficial

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import net.accelf.itc_lms_unofficial.databinding.FragmentLoginRequestBinding
import net.accelf.itc_lms_unofficial.util.isNotNullOrEmpty

class LoginRequestFragment : Fragment(R.layout.fragment_login_request) {

    private var _binding: FragmentLoginRequestBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginRequestBinding.bind(view)

        binding.textLoginInstruction.text = getString(R.string.login_instruction)

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.buttonLogin.isEnabled = editTextContents().first
            }
        }
        listOf(binding.editTextUserName, binding.editTextPassword).forEach {
            it.addTextChangedListener(textWatcher)
        }

        binding.buttonLogin.apply {
            isEnabled = false

            setOnClickListener {
                val (areNullOrEmpty, userName, password) = editTextContents()
                if (areNullOrEmpty && activity is LoginActivity) {
                    (activity as LoginActivity).onLoginClick(userName, password)
                }
            }
        }
    }

    private fun editTextContents(): Triple<Boolean, String, String> {
        val userName = binding.editTextUserName.text?.trim()
        val password = binding.editTextPassword.text?.trim()
        return Triple(
            userName.isNotNullOrEmpty() && password.isNotNullOrEmpty(),
            userName.toString(),
            password.toString()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(): LoginRequestFragment {
            return LoginRequestFragment()
        }
    }
}
