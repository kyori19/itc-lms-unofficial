package net.accelf.itc_lms_unofficial.timetable

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import net.accelf.itc_lms_unofficial.models.TimeTable
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.util.Request
import net.accelf.itc_lms_unofficial.util.RxAwareViewModel
import net.accelf.itc_lms_unofficial.util.mutableRequestOf

class TimeTableViewModel @ViewModelInject constructor(
    private val lms: LMS,
) : RxAwareViewModel() {

    private val mutableTimeTable = mutableRequestOf<TimeTable>()
    val timeTable: LiveData<Request<TimeTable>> = mutableTimeTable

    fun load(year: String = "", term: String = "") {
        lms.getTimeTable(year, term)
            .toLiveData(mutableTimeTable)
    }
}
