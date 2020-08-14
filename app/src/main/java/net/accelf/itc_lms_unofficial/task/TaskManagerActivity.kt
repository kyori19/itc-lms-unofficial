package net.accelf.itc_lms_unofficial.task

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import net.accelf.itc_lms_unofficial.BaseActivity
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.util.replaceFragment

class TaskManagerActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        replaceFragment(TaskManagerFragment.newInstance())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menu?.findItem(R.id.actionOpenTaskManager)?.isVisible = false
        return true
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, TaskManagerActivity::class.java)
        }
    }
}
