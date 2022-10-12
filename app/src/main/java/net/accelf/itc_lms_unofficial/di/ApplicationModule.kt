package net.accelf.itc_lms_unofficial.di

import android.net.Uri
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.accelf.itc_lms_unofficial.BuildConfig
import net.accelf.itc_lms_unofficial.network.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import java.lang.reflect.Proxy
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun provideLmsClient(
        cookieJar: SavedCookieJar,
        gson: Gson,
    ): LMS {
        val okHttpClient = OkHttpClient.Builder().apply {
            addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE)
            )
            cookieJar(cookieJar)
            followRedirects(false)
        }.build()

        val lms = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(lmsHostUrl)
            .addConverterFactory(DocumentConverterFactory(gson))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
            .create(LMS::class.java)

        return Proxy.newProxyInstance(
            LMS::class.java.classLoader,
            arrayOf(LMS::class.java, ProxyInterface::class.java),
            LMSProxy(lms),
        ) as LMS
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(Uri::class.java, JsonUriAdapter())
            .create()
    }

    @Provides
    @Singleton
    fun provideNotificationId(): AtomicInteger {
        return AtomicInteger(0)
    }
}
