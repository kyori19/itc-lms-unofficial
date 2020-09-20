package net.accelf.itc_lms_unofficial.network

import android.content.Context
import net.accelf.itc_lms_unofficial.LoginActivity
import okhttp3.Interceptor
import okhttp3.Response

class EmptyResponseInterceptor(
    private val context: Context,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (response.code == 200
            && response.body?.contentLength()?.toInt() == 0
            && request.url.encodedPath == "/lms/timetable"
        ) {
            context.startActivity(LoginActivity.intent(context, true))
        }
        return response
    }
}
