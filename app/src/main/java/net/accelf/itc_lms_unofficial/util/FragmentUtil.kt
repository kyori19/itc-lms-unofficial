package net.accelf.itc_lms_unofficial.util

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import net.accelf.itc_lms_unofficial.ErrorFragment
import net.accelf.itc_lms_unofficial.R

fun AppCompatActivity.replaceFragment(fragment: Fragment, @IdRes target: Int? = null) {
    supportFragmentManager.beginTransaction().apply {
        replace(target ?: R.id.content, fragment)
        commit()
    }
}

fun AppCompatActivity.replaceErrorFragment(message: String?, @IdRes target: Int? = null) {
    replaceFragment(ErrorFragment.newInstance(message), target)
}

fun AppCompatActivity.replaceErrorFragment(throwable: Throwable, @IdRes target: Int? = null) {
    replaceErrorFragment(throwable.localizedMessage, target)
    throwable.printStackTrace()
}
