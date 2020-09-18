package net.accelf.itc_lms_unofficial.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import kotlin.math.roundToInt

/**
 * @see androidx.recyclerview.widget.DividerItemDecoration
 */
class BetweenDividerDecoration(context: Context, private val orientation: Int = VERTICAL) :
    ItemDecoration() {

    private val drawable: Drawable
    private val mBounds = Rect()

    init {
        val a = context.obtainStyledAttributes(ATTRS)
        drawable =
            requireNotNull(a.getDrawable(0)) { "@android:attr/listDivider was not set in the theme used for this DividerItemDecoration. Please set that attribute all call setDrawable()" }
        a.recycle()
        require(orientation in listOf(HORIZONTAL,
            VERTICAL)) { "Invalid orientation. It should be either HORIZONTAL or VERTICAL" }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager == null) {
            return
        }
        when (orientation) {
            VERTICAL -> drawVertical(c, parent)
            HORIZONTAL -> drawHorizontal(c, parent)
        }
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val left: Int
        val right: Int
        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(left, parent.paddingTop, right,
                parent.height - parent.paddingBottom)
        } else {
            left = 0
            right = parent.width
        }
        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, mBounds)
            val bottom = mBounds.bottom + child.translationY.roundToInt()
            val top = bottom - drawable.intrinsicHeight
            drawable.setBounds(left, top, right, bottom)
            drawable.draw(canvas)
        }
        canvas.restore()
    }

    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val top: Int
        val bottom: Int
        if (parent.clipToPadding) {
            top = parent.paddingTop
            bottom = parent.height - parent.paddingBottom
            canvas.clipRect(parent.paddingLeft, top,
                parent.width - parent.paddingRight, bottom)
        } else {
            top = 0
            bottom = parent.height
        }
        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)
            parent.layoutManager!!.getDecoratedBoundsWithMargins(child, mBounds)
            val right = mBounds.right + child.translationX.roundToInt()
            val left = right - drawable.intrinsicWidth
            drawable.setBounds(left, top, right, bottom)
            drawable.draw(canvas)
        }
        canvas.restore()
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        val position = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
        val count = state.itemCount

        when {
            position == count - 1 -> {
                outRect.setEmpty()
            }
            orientation == VERTICAL -> {
                outRect.set(0, 0, 0, drawable.intrinsicHeight)
            }
            else -> {
                outRect.set(0, 0, drawable.intrinsicWidth, 0)
            }
        }
    }

    companion object {
        const val HORIZONTAL = LinearLayout.HORIZONTAL
        const val VERTICAL = LinearLayout.VERTICAL
        private val ATTRS = intArrayOf(android.R.attr.listDivider)
    }
}
