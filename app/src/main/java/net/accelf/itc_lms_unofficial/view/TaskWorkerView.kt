package net.accelf.itc_lms_unofficial.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.android.material.card.MaterialCardView
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.databinding.ViewTaskWorkerBinding
import net.accelf.itc_lms_unofficial.util.set

class TaskWorkerView(context: Context, attrs: AttributeSet?) : MaterialCardView(context, attrs) {

    private val title: String

    var tag: String = ""
        set(value) {
            field = value

            workManager.getWorkInfosByTagLiveData(tag)
                .observe(findViewTreeLifecycleOwner()!!, {
                    binding.listWorkers.set<WorkInfo, WorkersAdapter>(it)
                })
        }

    private val workManager: WorkManager by lazy {
        WorkManager.getInstance(context)
    }

    val binding = ViewTaskWorkerBinding.inflate(LayoutInflater.from(context), this, true)

    init {
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

        binding.titleTask.text = title

        binding.buttonDeleteAll.setOnClickListener {
            workManager.cancelAllWorkByTag(tag)
        }
    }

    fun setOnEnqueueClickListener(listener: (View) -> Unit) {
        binding.buttonEnqueue.apply {
            visibility = VISIBLE
            setOnClickListener(listener)
        }
    }
}
