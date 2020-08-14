package net.accelf.itc_lms_unofficial.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.lifecycle.Observer
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.view_task_worker.view.*
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.task.PullUpdatesWorker
import net.accelf.itc_lms_unofficial.util.set

class TaskWorkerView(context: Context, attrs: AttributeSet?) : MaterialCardView(context, attrs) {

    private val title: String

    var tag: String = ""
        set(value) {
            field = value

            workManager.getWorkInfosByTagLiveData(tag)
                .observe(findViewTreeLifecycleOwner()!!, Observer {
                    @Suppress("UNCHECKED_CAST")
                    listWorkers.set(
                        it,
                        WorkersAdapter::class.java as Class<RecyclerView.Adapter<RecyclerView.ViewHolder>>
                    )
                })
        }

    private val workManager: WorkManager by lazy {
        WorkManager.getInstance(context)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_task_worker, this, true)

        context.theme.obtainStyledAttributes(attrs, R.styleable.TaskWorkerView, 0, 0).apply {
            try {
                title = getString(R.styleable.TaskWorkerView_title) ?: ""
            } finally {
                recycle()
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        titleTask.text = title

        buttonEnqueue.setOnClickListener {
            PullUpdatesWorker.enqueue(context, true)
        }

        buttonDeleteAll.setOnClickListener {
            workManager.cancelAllWorkByTag(tag)
        }
    }
}
