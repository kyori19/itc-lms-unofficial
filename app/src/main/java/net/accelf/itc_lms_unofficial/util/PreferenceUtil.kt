package net.accelf.itc_lms_unofficial.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

val Context.defaultSharedPreference: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(this)
