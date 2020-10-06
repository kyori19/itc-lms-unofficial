package net.accelf.itc_lms_unofficial.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import net.accelf.itc_lms_unofficial.models.SelectOption.Companion.selectedValue
import net.accelf.itc_lms_unofficial.models.Settings
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.util.Request
import net.accelf.itc_lms_unofficial.util.RxAwareViewModel
import net.accelf.itc_lms_unofficial.util.Success
import net.accelf.itc_lms_unofficial.util.mutableRequestOf

class PreferenceViewModel @ViewModelInject constructor(
    private val lms: LMS,
) : RxAwareViewModel() {

    private val mutableSettings = mutableRequestOf<Settings>()
    val settings: LiveData<Request<Settings>> = mutableSettings

    init {
        load()
    }

    private fun load() {
        lms.getSettings()
            .toLiveData(mutableSettings)
    }

    fun setLanguage(value: String) {
        val settings = (this.settings.value as Success).data
        lms.updateSettings(
            settings.csrf,
            value,
            settings.mailType,
            settings.mailAddress1,
            settings.mailAddress2,
            settings.notifyForwardOptions.selectedValue(),
            settings.updateForwardOptions.selectedValue(),
        )
            .subscribe { _, _ ->
                load()
            }
            .autoDispose()
    }
}
