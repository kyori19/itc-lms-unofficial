package net.accelf.itc_lms_unofficial.task

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.work.WorkManager
import net.accelf.itc_lms_unofficial.BaseActivity
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.util.replaceFragment

class TaskManagerActivity : BaseActivity(false) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        replaceFragment(TaskManagerFragment.newInstance())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)

        menu.apply {
            findItem(R.id.actionOpenTaskManager)?.isVisible = false
            findItem(R.id.actionFlush)?.isVisible = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionFlush -> {
                WorkManager.getInstance(this)
                    .pruneWork()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
