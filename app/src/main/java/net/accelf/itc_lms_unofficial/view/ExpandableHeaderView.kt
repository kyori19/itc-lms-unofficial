package net.accelf.itc_lms_unofficial.view

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.view_expandable_header.view.*
import net.accelf.itc_lms_unofficial.R

class ExpandableHeaderView(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    private val text: String
    private val iconContentDescription: String

    @IdRes
    private val targetLayoutId: Int

    init {
        LayoutInflater.from(context).inflate(R.layout.view_expandable_header, this, true)

        context.theme.obtainStyledAttributes(attrs, R.styleable.ExpandableHeaderView, 0, 0).apply {
            try {
                text = getString(R.styleable.ExpandableHeaderView_text) ?: ""
                iconContentDescription =
                    getString(R.styleable.ExpandableHeaderView_iconContentDescription) ?: ""
                targetLayoutId = getResourceId(R.styleable.ExpandableHeaderView_targetLayout, 0)
            } finally {
                recycle()
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val expandableLayout =
            (parent as View).findViewById<ExpandableConstraintLayout>(targetLayoutId)

        setOnClickListener {
            expandableLayout.toggle()

            iconExpand.apply {
                val open = expandableLayout.isExpanded

                val from = when (open) {
                    true -> 180f
                    false -> 0f
                }
                val to = when (open) {
                    true -> 0f
                    false -> 180f
                }

                ObjectAnimator.ofFloat(this, "rotation", from, to).start()
            }
        }

        textTitle.text = text
        iconExpand.apply {
            contentDescription = iconContentDescription
            rotation = when (expandableLayout.isExpanded) {
                true -> 0f
                false -> 180f
            }
        }
    }
}
