package net.accelf.itc_lms_unofficial.task

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_task_manager.*
import net.accelf.itc_lms_unofficial.R

class TaskManagerFragment : Fragment(R.layout.fragment_task_manager) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewWorkerPullUpdates.tag = PullUpdatesWorker::class.java.name
    }

    companion object {
        @JvmStatic
        fun newInstance(): TaskManagerFragment {
            return TaskManagerFragment()
        }
    }
}
