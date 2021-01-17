package net.accelf.itc_lms_unofficial.coursedetail

import net.accelf.itc_lms_unofficial.databinding.ItemForumBinding
import net.accelf.itc_lms_unofficial.models.Forum
import net.accelf.itc_lms_unofficial.util.UpdatableAdapter
import net.accelf.itc_lms_unofficial.util.timeSpanToString

class ForumsAdapter(
    items: List<Forum>,
) : UpdatableAdapter<Forum, ItemForumBinding>(items, ItemForumBinding::class.java) {

    override fun onBindViewHolder(holder: ViewHolder<ItemForumBinding>, position: Int) {
        val item = items[position]

        holder.binding.apply {
            textForumTitle.text = item.title
            textForumDate.apply {
                text = context.timeSpanToString(item.from, item.until)
            }
        }
    }
}
