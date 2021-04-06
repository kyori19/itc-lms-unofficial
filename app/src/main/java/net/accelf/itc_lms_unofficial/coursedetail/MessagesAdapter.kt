package net.accelf.itc_lms_unofficial.coursedetail

import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.databinding.ItemMessageBinding
import net.accelf.itc_lms_unofficial.models.Message
import net.accelf.itc_lms_unofficial.util.TIME_FORMAT
import net.accelf.itc_lms_unofficial.util.UpdatableAdapter

class MessagesAdapter(
    items: List<Message>,
) : UpdatableAdapter<Message, ItemMessageBinding>(items, ItemMessageBinding::class.java) {

    override fun onBindViewHolder(holder: ViewHolder<ItemMessageBinding>, position: Int) {
        val item = items[position]

        holder.binding.apply {
            iconMessageStatus.apply {
                setImageResource(
                    when (item.status) {
                        Message.MessageStatus.WAITING_FOR_ANSWER -> R.drawable.ic_notification_none
                        Message.MessageStatus.HAS_ANSWER -> R.drawable.ic_notification_available
                        Message.MessageStatus.COMPLETED -> R.drawable.ic_check
                    }
                )
                contentDescription = context.getString(
                    when (item.status) {
                        Message.MessageStatus.WAITING_FOR_ANSWER -> R.string.hint_icon_waiting_for_answer
                        Message.MessageStatus.HAS_ANSWER -> R.string.hint_icon_has_answer
                        Message.MessageStatus.COMPLETED -> R.string.hint_icon_completed
                    }
                )
            }

            titleMessage.text = item.title
            textMessageDate.text = item.createdAt?.let { TIME_FORMAT.format(it) }

            textLatestInfo.apply {
                text = when (item.status) {
                    Message.MessageStatus.WAITING_FOR_ANSWER -> context.getString(R.string.text_message_sent)
                    Message.MessageStatus.HAS_ANSWER -> context.getString(
                        R.string.text_message_got_answer_at,
                        TIME_FORMAT.format(item.actedAt!!)
                    )
                    Message.MessageStatus.COMPLETED -> context.getString(
                        R.string.text_message_completed_by,
                        item.actorName
                    )
                }
            }
        }
    }
}
