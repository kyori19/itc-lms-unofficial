package net.accelf.itc_lms_unofficial.reportdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_file.view.*
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.models.SubmittedFile
import net.accelf.itc_lms_unofficial.util.TIME_FORMAT

class SubmittedFileAdapter(
    val items: List<SubmittedFile>,
    private val listener: SubmittedFileListener,
) : RecyclerView.Adapter<SubmittedFileAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_file, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.apply {
            root.setOnClickListener {
                listener.openFile(item.file)
            }

            textReportFileName.text = item.file.fileName

            textReportFileDate.text = TIME_FORMAT.format(item.submittedAt)
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val textReportFileName: TextView = view.textReportFileName
        val textReportFileDate: TextView = view.textReportFileDate
    }
}
