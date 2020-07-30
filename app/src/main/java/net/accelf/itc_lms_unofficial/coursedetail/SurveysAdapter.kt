package net.accelf.itc_lms_unofficial.coursedetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.models.Survey
import net.accelf.itc_lms_unofficial.util.timeSpanToString

class SurveysAdapter(
    private val items: List<Survey>
) : RecyclerView.Adapter<SurveysAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_survey, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.apply {
            textSurveyTitle.text = item.title
            textSurveyDate.apply {
                text = context.timeSpanToString(item.from, item.until)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textSurveyTitle: TextView = view.findViewById(R.id.textSurveyTitle)
        val textSurveyDate: TextView = view.findViewById(R.id.textSurveyDate)
    }
}
