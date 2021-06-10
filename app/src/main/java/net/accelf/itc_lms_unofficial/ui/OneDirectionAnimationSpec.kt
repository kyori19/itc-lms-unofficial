package net.accelf.itc_lms_unofficial.ui

import androidx.compose.animation.core.FloatAnimationSpec
import androidx.compose.animation.core.FloatSpringSpec
import androidx.compose.animation.core.Spring

class OneDirectionFloatSpringSpec(
    from: Float = 0f,
    private val toExclusive: Float = 360f,
    dampingRatio: Float = Spring.DampingRatioNoBouncy,
    stiffness: Float = Spring.StiffnessMedium,
    visibilityThreshold: Float = Spring.DefaultDisplacementThreshold,
) : FloatAnimationSpec {

    private val springSpec = FloatSpringSpec(dampingRatio, stiffness, visibilityThreshold)
    private val one = toExclusive - from

    private fun virtualTargetValue(initialValue: Float, targetValue: Float): Float {
        return when {
            initialValue > targetValue -> targetValue + one
            else -> targetValue
        }
    }

    override fun getDurationNanos(
        initialValue: Float,
        targetValue: Float,
        initialVelocity: Float,
    ): Long {
        return springSpec.getDurationNanos(
            initialValue,
            virtualTargetValue(initialValue, targetValue),
            initialVelocity,
        )
    }

    override fun getValueFromNanos(
        playTimeNanos: Long,
        initialValue: Float,
        targetValue: Float,
        initialVelocity: Float,
    ): Float {
        val value = springSpec.getValueFromNanos(
            playTimeNanos,
            initialValue,
            virtualTargetValue(initialValue, targetValue),
            initialVelocity,
        )
        return when {
            value >= toExclusive -> value - one
            else -> value
        }
    }

    override fun getVelocityFromNanos(
        playTimeNanos: Long,
        initialValue: Float,
        targetValue: Float,
        initialVelocity: Float,
    ): Float {
        return springSpec.getVelocityFromNanos(
            playTimeNanos,
            initialValue,
            virtualTargetValue(initialValue, targetValue),
            initialVelocity,
        )
    }
}
