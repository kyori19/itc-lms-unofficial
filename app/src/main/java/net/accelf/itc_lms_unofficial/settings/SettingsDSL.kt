/**
 * Mostly copied from tuskyapp/Tusky
 * https://github.com/tuskyapp/Tusky/blob/ecb94beb3cf656b62f4eccaa1f87d1342db82af6/app/src/main/java/com/keylesspalace/tusky/settings/SettingsDSL.kt
 * GNU General Public License v3.0
 */

package net.accelf.itc_lms_unofficial.settings

import android.content.Context
import androidx.annotation.StringRes
import androidx.preference.*

class PreferenceParent(
    val context: Context,
    val addPref: (pref: Preference) -> Unit,
    val applyToAll: Preference.() -> Unit,
)

inline fun PreferenceParent.preference(builder: Preference.() -> Unit): Preference {
    val pref = Preference(context)
    pref.apply(applyToAll)
    builder(pref)
    addPref(pref)
    return pref
}

inline fun PreferenceParent.listPreference(builder: ListPreference.() -> Unit): ListPreference {
    val pref = ListPreference(context)
    pref.apply(applyToAll)
    builder(pref)
    addPref(pref)
    return pref
}

inline fun PreferenceParent.switchPreference(
    builder: SwitchPreference.() -> Unit,
): SwitchPreference {
    val pref = SwitchPreference(context)
    pref.apply(applyToAll)
    builder(pref)
    addPref(pref)
    return pref
}

inline fun PreferenceParent.editTextPreference(
    builder: EditTextPreference.() -> Unit,
): EditTextPreference {
    val pref = EditTextPreference(context)
    pref.apply(applyToAll)
    builder(pref)
    addPref(pref)
    return pref
}

inline fun PreferenceParent.preferenceCategory(
    @StringRes title: Int,
    builder: PreferenceParent.(PreferenceCategory) -> Unit,
) {
    val category = PreferenceCategory(context)
    category.apply(applyToAll)
    addPref(category)
    category.setTitle(title)
    val newParent = PreferenceParent(context, { category.addPreference(it) }) {
        apply(applyToAll)
    }
    builder(newParent, category)
}

inline fun PreferenceFragmentCompat.makePreferenceScreen(
    builder: PreferenceParent.() -> Unit,
): PreferenceScreen {
    val context = requireContext()
    val screen = preferenceManager.createPreferenceScreen(context)
    val parent = PreferenceParent(context, { screen.addPreference(it) }) {
        isIconSpaceReserved = false
        isSingleLineTitle = false
    }
    // For some functions (like dependencies) it's much easier for us if we attach screen first
    // and change it later
    preferenceScreen = screen
    builder(parent)
    return screen
}
