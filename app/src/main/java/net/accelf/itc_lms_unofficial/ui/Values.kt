package net.accelf.itc_lms_unofficial.ui

import androidx.annotation.FloatRange
import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

object Values {

    object Theme {
        val colors = darkColors(
            primary = Color(0xfff67504),
            primaryVariant = Color(0xffc45d03),
            secondary = Color(0xffdaf420),
        )

        val Colors.success: Color
            @Composable get() = if (isLight) Color(0xff20b020) else Color(0xff5cb95c)

        val Colors.warning: Color
            @Composable get() = if (isLight) Color(0xfffbc02d) else Color(0xffd6b052)

        @Composable
        fun Colors.gray(@FloatRange(from = 0.0, to = 1.0) level: Double): Color {
            val lighten = (0xff * (if (isLight) 1.0 - level else level)).roundToInt()
            return Color(lighten, lighten, lighten)
        }
    }

    object Spacing {
        val normal = 4.dp
        val around = normal / 2
    }

    object Text {
        val large = 24.sp
        val small = 12.sp
    }
}
