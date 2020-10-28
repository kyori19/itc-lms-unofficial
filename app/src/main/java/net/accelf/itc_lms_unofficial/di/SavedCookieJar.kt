package net.accelf.itc_lms_unofficial.di

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import net.accelf.itc_lms_unofficial.Prefs
import net.accelf.itc_lms_unofficial.network.lmsHostUrl
import net.accelf.itc_lms_unofficial.util.defaultSharedPreference
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedCookieJar @Inject constructor(@ApplicationContext private val context: Context) :
    CookieJar {

    private lateinit var cookies: List<Cookie>

    init {
        loadCookies()
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
            .putStringSet(Prefs.Keys.COOKIE, cookieSet)
            .apply()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookies
    }

    fun loadCookies() {
        this.cookies = context.defaultSharedPreference
            .getStringSet(Prefs.Keys.COOKIE, setOf())?.mapNotNull {
                Cookie.parse(lmsHostUrl, it)
            } ?: listOf()
    }
}
