package net.accelf.itc_lms_unofficial.timetable

import androidx.lifecycle.LiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import net.accelf.itc_lms_unofficial.models.TimeTable
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.util.Request
import net.accelf.itc_lms_unofficial.util.RxAwareViewModel
import net.accelf.itc_lms_unofficial.util.mutableRequestOf
import javax.inject.Inject

@HiltViewModel
class TimeTableViewModel @Inject constructor(
    private val lms: LMS,
) : RxAwareViewModel() {

    private val mutableTimeTable = mutableRequestOf<TimeTable>()
    val timeTable: LiveData<Request<TimeTable>> = mutableTimeTable

    init {
        load()
    }

    fun load(year: String = "", term: String = "") {
        lms.getTimeTable(year, term)
            .toLiveData(mutableTimeTable)
    }
}
