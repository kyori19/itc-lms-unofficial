package net.accelf.itc_lms_unofficial.reportdetail

import net.accelf.itc_lms_unofficial.databinding.ItemFileBinding
import net.accelf.itc_lms_unofficial.models.SubmittedFile
import net.accelf.itc_lms_unofficial.util.BaseAdapter
import net.accelf.itc_lms_unofficial.util.TIME_FORMAT

class SubmittedFileAdapter(
    items: List<SubmittedFile>,
    private val listener: SubmittedFileListener,
) : BaseAdapter<SubmittedFile, ItemFileBinding>(items, ItemFileBinding::class.java) {

    override fun onBindViewHolder(holder: ViewHolder<ItemFileBinding>, position: Int) {
        val item = items[position]

        holder.binding.apply {
            root.setOnClickListener {
                listener.openFile(item.file)
            }

            textReportFileName.text = item.file.fileName

            textReportFileDate.text = TIME_FORMAT.format(item.submittedAt)
        }
    }
}
