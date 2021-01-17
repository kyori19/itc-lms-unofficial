package net.accelf.itc_lms_unofficial.view

import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.work.WorkInfo
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.databinding.ItemWorkerBinding
import net.accelf.itc_lms_unofficial.util.UpdatableAdapter
import net.accelf.itc_lms_unofficial.util.getColorFromAttr

class WorkersAdapter(items: List<WorkInfo>) :
    UpdatableAdapter<WorkInfo, ItemWorkerBinding>(items, ItemWorkerBinding::class.java) {

    override fun onBindViewHolder(holder: ViewHolder<ItemWorkerBinding>, position: Int) {
        val item = items[position]

        holder.binding.apply {
            textWorkerState.apply {
                text = item.state.toString()

                setTextColor(
                    context.getColorFromAttr(
                        when (item.state) {
                            WorkInfo.State.ENQUEUED -> R.attr.colorWarning
                            WorkInfo.State.RUNNING -> R.attr.colorSuccess
                            WorkInfo.State.CANCELLED -> R.attr.colorError
                            else -> android.R.attr.colorForeground
                        }
                    )
                )
            }

            textWorkerId.text = item.id.toString()

            textWorkerMessage.apply {
                val message = if (item.state == WorkInfo.State.RUNNING) {
                    item.progress.getString(DATA_MESSAGE)
                } else {
                    item.outputData.getString(DATA_MESSAGE)
                }

                visibility = if (message.isNullOrEmpty()) {
                    GONE
                } else {
                    VISIBLE
                }
                text = message
            }
        }
    }

    companion object {
        const val DATA_MESSAGE = "message"
    }
}
