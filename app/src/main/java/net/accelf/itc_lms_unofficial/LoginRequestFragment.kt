package net.accelf.itc_lms_unofficial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_login_request.*
import net.accelf.itc_lms_unofficial.util.isNotNullOrEmpty

class LoginRequestFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textLoginInstruction.text = getString(R.string.login_instruction)

        buttonLogin.setOnClickListener {
            val userName = editTextUserName.text?.trim()
            val password = editTextPassword.text?.trim()
            if (userName.isNotNullOrEmpty() && password.isNotNullOrEmpty()
                && activity is LoginActivity
            ) {
                (activity as LoginActivity).onLoginClick(userName.toString(), password.toString())
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): LoginRequestFragment {
            return LoginRequestFragment()
        }
    }
}
