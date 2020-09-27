package net.accelf.itc_lms_unofficial.di

import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.MotionEvent
import android.widget.TextView
import androidx.core.text.getSpans
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomLinkMovementMethod @Inject constructor() : LinkMovementMethod() {

    override fun onTouchEvent(widget: TextView?, buffer: Spannable?, event: MotionEvent?): Boolean {
        val action = event!!.action

        if (action == MotionEvent.ACTION_UP) {
            var x = event.x.toInt()
            var y = event.y.toInt()
            x -= widget!!.totalPaddingLeft
            y -= widget.totalPaddingTop
            x += widget.scrollX
            y += widget.scrollY

            val layout = widget.layout
            val line = layout.getLineForVertical(y)
            val off = layout.getOffsetForHorizontal(line, x.toFloat())

            val links = buffer!!.getSpans<URLSpan>(off, off)
            if (links.isNotEmpty()) {
                val link = links[0]
                link.onClick(widget)
                return true
            }
        }

        return super.onTouchEvent(widget, buffer, event)
    }
}
