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
        snackbar = Snackbar.make(view, R.string.snackbar_hint_restart, Snackbar.LENGTH_INDEFINITE)
            .apply {
                setAction(R.string.snackbar_button_restart) {
                    restartApp()
                }

                show()
            }
    }
}
