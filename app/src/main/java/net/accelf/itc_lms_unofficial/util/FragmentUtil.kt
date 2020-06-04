package net.accelf.itc_lms_unofficial.util

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import net.accelf.itc_lms_unofficial.ErrorFragment

fun AppCompatActivity.replaceFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction().apply {
        replace(android.R.id.content, fragment)
        commit()
    }
}

fun AppCompatActivity.replaceErrorFragment(message: String?) {
    replaceFragment(ErrorFragment.newInstance(message))
}

fun AppCompatActivity.replaceErrorFragment(throwable: Throwable) {
    replaceErrorFragment(throwable.localizedMessage)
}
