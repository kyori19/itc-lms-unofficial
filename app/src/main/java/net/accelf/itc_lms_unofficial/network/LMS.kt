package net.accelf.itc_lms_unofficial.network

import io.reactivex.Single
import net.accelf.itc_lms_unofficial.models.TimeTable
import retrofit2.http.GET

interface LMS {

    @GET("lms/timetable")
    fun getTimeTable(): Single<TimeTable>

}
