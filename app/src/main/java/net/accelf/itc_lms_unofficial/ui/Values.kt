package net.accelf.itc_lms_unofficial.ui

import androidx.compose.material.darkColors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object Values {

    object Colors {
        val theme = darkColors(
            primary = Color(0xfff67504),
            primaryVariant = Color(0xffc45d03),
            secondary = Color(0xffdaf420),
        )

        object Gray {
            val darken = Color(0xff616161)
            val surface = Color(0xff212121)
        }
    }

    object Spacing {
        private val normal = 4.dp
        val around = normal / 2
    }

    object Text {
        val large = 24.sp
        val small = 12.sp
    }
}
