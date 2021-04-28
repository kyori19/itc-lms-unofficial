package net.accelf.itc_lms_unofficial

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import net.accelf.itc_lms_unofficial.util.restartApp
import kotlin.concurrent.thread

open class ActionableFragment(
    private val delayMillis: Long = 0,
) : Fragment() {

    enum class ActionType {
        BACK_TO_MAIN,
        RETRY_LOGIN,
    }

    var actionType = ActionType.BACK_TO_MAIN

    private var snackbar: Snackbar? = null

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

    override fun onDestroy() {
        super.onDestroy()

        snackbar?.dismiss()
    }

    private fun showAction(view: View) {
        when (actionType) {
            ActionType.BACK_TO_MAIN -> {
                snackbar =
                    Snackbar.make(view, R.string.snackbar_hint_restart, Snackbar.LENGTH_INDEFINITE)
                        .apply {
                            setAction(R.string.snackbar_button_restart) {
                                restartApp()
                            }

                            show()
                        }
            }
            ActionType.RETRY_LOGIN -> {
                snackbar =
                    Snackbar.make(view, R.string.snackbar_hint_re_login, Snackbar.LENGTH_INDEFINITE)
                        .apply {
                            setAction(R.string.snackbar_button_re_login) {
                                if (activity is LoginActivity) {
                                    (activity as LoginActivity).retryLogin()
                                }
                            }

                            show()
                        }
            }
        }
    }
}
