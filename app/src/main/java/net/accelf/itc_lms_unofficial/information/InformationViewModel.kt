package net.accelf.itc_lms_unofficial.information

import androidx.lifecycle.LiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import net.accelf.itc_lms_unofficial.models.Information
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.util.Request
import net.accelf.itc_lms_unofficial.util.RxAwareViewModel
import net.accelf.itc_lms_unofficial.util.mutableRequestOf
import javax.inject.Inject

@HiltViewModel
class InformationViewModel @Inject constructor(
    private val lms: LMS,
) : RxAwareViewModel() {

    private val mutableInformation = mutableRequestOf<Information>()
    val information: LiveData<Request<Information>> = mutableInformation

    init {
        load()
    }

    fun load() {
        lms.getInformation()
            .toLiveData(mutableInformation)
    }
}
