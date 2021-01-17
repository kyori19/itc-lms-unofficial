package net.accelf.itc_lms_unofficial.task

import android.os.Bundle
import android.view.View
import net.accelf.itc_lms_unofficial.BaseFragment
import net.accelf.itc_lms_unofficial.databinding.FragmentTaskManagerBinding
import net.accelf.itc_lms_unofficial.file.download.FileDownloadWorker

class TaskManagerFragment :
    BaseFragment<FragmentTaskManagerBinding>(FragmentTaskManagerBinding::class.java) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewWorkerPullUpdates.apply {
            tag = PullUpdatesWorker::class.java.name

            setOnEnqueueClickListener {
                PullUpdatesWorker.enqueue(it.context, true)
            }
        }

        binding.viewWorkerFileDownload.tag = FileDownloadWorker::class.java.name
    }

    companion object {
        @JvmStatic
        fun newInstance(): TaskManagerFragment {
            return TaskManagerFragment()
        }
    }
}
