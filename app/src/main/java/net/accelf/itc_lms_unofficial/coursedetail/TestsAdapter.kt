package net.accelf.itc_lms_unofficial.coursedetail

import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.databinding.ItemTestBinding
import net.accelf.itc_lms_unofficial.models.Test
import net.accelf.itc_lms_unofficial.util.UpdatableAdapter
import net.accelf.itc_lms_unofficial.util.timeSpanToString

class TestsAdapter(
    items: List<Test>,
) : UpdatableAdapter<Test, ItemTestBinding>(items, ItemTestBinding::class.java) {

    override fun onBindViewHolder(holder: ViewHolder<ItemTestBinding>, position: Int) {
        val item = items[position]

        holder.binding.apply {
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
}
