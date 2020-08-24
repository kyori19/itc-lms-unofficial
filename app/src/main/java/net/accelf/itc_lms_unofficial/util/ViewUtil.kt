package net.accelf.itc_lms_unofficial.util

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE

fun showViewsAndDoWhen(condition: Boolean, vararg view: View, onVisible: () -> Unit) {
    if (condition) {
        view.forEach {
            it.visibility = VISIBLE
        }

        onVisible()
    } else {
        view.forEach {
            it.visibility = GONE
        }
    }
}
