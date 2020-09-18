package net.accelf.itc_lms_unofficial.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import dagger.hilt.android.AndroidEntryPoint
import net.accelf.itc_lms_unofficial.BaseActivity
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.util.replaceFragment

@AndroidEntryPoint
class PreferenceActivity : BaseActivity(false) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        replaceFragment(PreferenceFragment.newInstance())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menu?.apply {
            findItem(R.id.actionSettings).isVisible = false
        }
        return true
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, PreferenceActivity::class.java)
        }
    }
}
