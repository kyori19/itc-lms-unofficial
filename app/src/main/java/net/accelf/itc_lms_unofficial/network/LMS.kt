package net.accelf.itc_lms_unofficial.network

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET

interface LMS {

    @GET("lms/timetable/log")
    fun getLog(): Single<ResponseBody>

}
