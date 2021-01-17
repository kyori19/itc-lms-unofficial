package net.accelf.itc_lms_unofficial.coursedetail

import net.accelf.itc_lms_unofficial.databinding.ItemNotifyBinding
import net.accelf.itc_lms_unofficial.models.Notify
import net.accelf.itc_lms_unofficial.util.UpdatableAdapter
import net.accelf.itc_lms_unofficial.util.timeSpanToString

class NotifiesAdapter(
    items: List<Notify>,
    private val listener: NotifyListener,
) : UpdatableAdapter<Notify, ItemNotifyBinding>(items, ItemNotifyBinding::class.java) {

    override fun onBindViewHolder(holder: ViewHolder<ItemNotifyBinding>, position: Int) {
        val item = items[position]

        holder.binding.apply {
            root.setOnClickListener {
                listener.openNotify(item.id)
            }

            textNotifyTitle.text = item.title
            textNotifyDate.apply {
                text = context.timeSpanToString(item.from, item.until)
            }
        }
    }
}
