package net.accelf.itc_lms_unofficial.coursedetail

import net.accelf.itc_lms_unofficial.databinding.ItemSurveyBinding
import net.accelf.itc_lms_unofficial.models.Survey
import net.accelf.itc_lms_unofficial.util.UpdatableAdapter
import net.accelf.itc_lms_unofficial.util.timeSpanToString

class SurveysAdapter(
    items: List<Survey>,
) : UpdatableAdapter<Survey, ItemSurveyBinding>(items, ItemSurveyBinding::class.java) {

    override fun onBindViewHolder(holder: ViewHolder<ItemSurveyBinding>, position: Int) {
        val item = items[position]

        holder.binding.apply {
            textSurveyTitle.text = item.title
            textSurveyDate.apply {
                text = context.timeSpanToString(item.from, item.until)
            }
        }
    }
}
