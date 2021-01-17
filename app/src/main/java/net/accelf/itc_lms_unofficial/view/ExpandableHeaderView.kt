package net.accelf.itc_lms_unofficial.view

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintLayout
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.databinding.ViewExpandableHeaderBinding

class ExpandableHeaderView(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    private val text: String
    private val iconContentDescription: String

    @IdRes
    private val targetLayoutId: Int

    val binding = ViewExpandableHeaderBinding.inflate(LayoutInflater.from(context), this, true)

    init {
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

            binding.iconExpand.apply {
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

        binding.textTitle.text = text
        binding.iconExpand.apply {
            contentDescription = iconContentDescription
            rotation = when (expandableLayout.isExpanded) {
                true -> 0f
                false -> 180f
            }
        }
    }
}
