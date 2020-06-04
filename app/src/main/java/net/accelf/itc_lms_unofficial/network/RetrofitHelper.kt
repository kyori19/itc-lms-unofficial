package net.accelf.itc_lms_unofficial.network

import android.content.Context
import net.accelf.itc_lms_unofficial.BuildConfig
import net.accelf.itc_lms_unofficial.util.defaultSharedPreference
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

const val PREF_COOKIE = "cookie"

private val lmsHostUrl = HttpUrl.Builder()
    .scheme("https")
    .host("itc-lms.ecc.u-tokyo.ac.jp")
    .build()

fun Context.lmsClient(): LMS {
    return Retrofit.Builder()
        .client(okHttpClient(this))
        .baseUrl(lmsHostUrl)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
        .build()
        .create(LMS::class.java)
}

private fun okHttpClient(context: Context): OkHttpClient {
    return OkHttpClient.Builder().apply {
        addInterceptor(
            HttpLoggingInterceptor()
                .setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE)
        )
        cookieJar(SavedCookieJar(context))
    }.build()
}

class SavedCookieJar(private val context: Context) : CookieJar {

    private var cookies: List<Cookie>

    init {
        this.cookies = context.defaultSharedPreference
            .getStringSet(PREF_COOKIE, setOf())?.mapNotNull {
                Cookie.parse(lmsHostUrl, it)
            } ?: listOf()
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val newCookies = this.cookies.union(cookies)
        this.cookies = newCookies.filterIndexed { i, cookie ->
            i == newCookies.indexOfLast { it.name == cookie.name }
        }
        val cookieSet = this.cookies.map {
            it.toString()
        }.toSet()
        context.defaultSharedPreference
            .edit()
            .putStringSet(PREF_COOKIE, cookieSet)
            .apply()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookies
    }
}
