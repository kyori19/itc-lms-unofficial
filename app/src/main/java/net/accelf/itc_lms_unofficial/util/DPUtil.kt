package net.accelf.itc_lms_unofficial.util

import android.content.res.Resources
import android.util.TypedValue
import android.util.TypedValue.applyDimension

val Float.dp
    get() = applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)
