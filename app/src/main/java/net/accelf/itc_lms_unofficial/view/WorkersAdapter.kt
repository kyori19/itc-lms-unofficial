package net.accelf.itc_lms_unofficial.view

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkInfo
import kotlinx.android.synthetic.main.item_worker.view.*
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.util.UpdatableAdapter
import net.accelf.itc_lms_unofficial.util.getColorFromAttr

class WorkersAdapter(items: List<WorkInfo>) :
    UpdatableAdapter<WorkInfo, WorkersAdapter.ViewHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_worker, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.apply {
            textWorkerState.apply {
                text = item.state.toString()

                setTextColor(
                    context.getColorFromAttr(
                        when (item.state) {
                            WorkInfo.State.ENQUEUED -> R.attr.colorWarning
                            WorkInfo.State.RUNNING -> R.attr.colorSuccess
                            WorkInfo.State.CANCELLED -> R.attr.colorError
                            else -> android.R.attr.textColor
                        }
                    )
                )
            }

            textWorkerId.text = item.id.toString()

            textWorkerMessage.apply {
                val message = item.outputData.getString(DATA_MESSAGE)

                visibility = if (message.isNullOrEmpty()) {
                    GONE
                } else {
                    VISIBLE
                }
                text = message
            }
        }
    }

    override fun getItemCount(): Int = items.size

    companion object {
        const val DATA_MESSAGE = "message"
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textWorkerState: TextView = view.textWorkerState
        val textWorkerId: TextView = view.textWorkerId
        val textWorkerMessage: TextView = view.textWorkerMessage
    }
}
