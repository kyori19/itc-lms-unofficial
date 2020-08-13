package net.accelf.itc_lms_unofficial.coursedetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_test.view.*
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.models.Test
import net.accelf.itc_lms_unofficial.util.timeSpanToString

class TestsAdapter(
    private val items: List<Test>
) : RecyclerView.Adapter<TestsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_test, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.apply {
            iconTestStatus.apply {
                setImageResource(
                    when (item.status) {
                        Test.TestStatus.NOT_TAKEN -> R.drawable.ic_cancel
                        Test.TestStatus.TAKEN -> R.drawable.ic_check
                        Test.TestStatus.UNKNOWN -> R.drawable.ic_none
                    }
                )
                contentDescription = context.getString(
                    when (item.status) {
                        Test.TestStatus.NOT_TAKEN -> R.string.hint_icon_not_taken
                        Test.TestStatus.TAKEN -> R.string.hint_icon_taken
                        Test.TestStatus.UNKNOWN -> R.string.hint_icon_unknown
                    }
                )
            }

            titleTest.text = item.title
            textTestDate.apply {
                text = context.timeSpanToString(item.from, item.until)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconTestStatus: ImageView = view.iconTestStatus
        val titleTest: TextView = view.titleTest
        val textTestDate: TextView = view.textTestDate
    }
}
