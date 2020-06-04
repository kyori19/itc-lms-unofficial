package net.accelf.itc_lms_unofficial

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlin.concurrent.thread

open class ActionableFragment(
    private val type: ActionType,
    private val delayMillis: Long = 0
) : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (delayMillis == 0L) {
            showAction(view)
            return
        }

        thread {
            Thread.sleep(delayMillis)
            if (isVisible) {
                showAction(view)
            }
        }
    }

    private fun showAction(view: View) {
        when (type) {
            ActionType.BACK_TO_MAIN -> {
                Snackbar.make(view, R.string.snackbar_hint_restart, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.snackbar_button_restart) {
                        startActivity(MainActivity.intent(requireContext()))
                        activity?.finish()
                    }
                    .show()
            }
            ActionType.RETRY_LOGIN -> {
                Snackbar.make(view, R.string.snackbar_hint_re_login, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.snackbar_button_re_login) {
                        if (activity is LoginActivity) {
                            (activity as LoginActivity).retryLogin()
                        }
                    }
                    .show()
            }
        }
    }

    companion object {
        enum class ActionType {
            BACK_TO_MAIN,
            RETRY_LOGIN
        }
    }
}
