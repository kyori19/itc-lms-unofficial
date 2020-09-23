package net.accelf.itc_lms_unofficial.view

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.WRAP_CONTENT
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart

class ExpandableConstraintLayout(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    private var currentHeight = 0
    private var isAnimating = false

    var isExpanded = false
        set(value) {
            if (isAnimating) {
                return
            }

            field = value
            move()
        }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (isAnimating) {
            return
        }

        if (h > 0) {
            if (currentHeight == 0 && !isExpanded) {
                move(0)
            }

            currentHeight = h
        }
    }

    private fun move(duration: Long = 300) {
        val from = when (isExpanded) {
            true -> 1
            false -> currentHeight
        }
        val to = when (isExpanded) {
            true -> currentHeight
            false -> 1
        }

        ValueAnimator.ofInt(from, to)
            .apply {
                this.duration = duration

                doOnStart {
                    isAnimating = true

                    visibility = if (duration == 0L) {
                        INVISIBLE
                    } else {
                        VISIBLE
                    }
                }
                addUpdateListener {
                    layoutParams.height = it.animatedValue as Int
                    requestLayout()
                }
                doOnEnd {
                    isAnimating = false

                    if (!isExpanded) {
                        visibility = GONE
                    }
                    layoutParams.height = WRAP_CONTENT
                    requestLayout()
                }

                start()
            }
    }

    fun toggle() {
        isExpanded = !isExpanded
    }
}
