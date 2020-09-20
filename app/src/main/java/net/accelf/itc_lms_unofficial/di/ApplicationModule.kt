package net.accelf.itc_lms_unofficial.di

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import net.accelf.itc_lms_unofficial.BuildConfig
import net.accelf.itc_lms_unofficial.network.DocumentConverterFactory
import net.accelf.itc_lms_unofficial.network.EmptyResponseInterceptor
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.network.lmsHostUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun provideLmsClient(
        @ApplicationContext context: Context,
        cookieJar: SavedCookieJar,
        gson: Gson,
    ): LMS {
        val okHttpClient = OkHttpClient.Builder().apply {
            addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE)
            )
            addInterceptor(EmptyResponseInterceptor(context))
            cookieJar(cookieJar)
            followRedirects(false)
        }.build()

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(lmsHostUrl)
            .addConverterFactory(DocumentConverterFactory(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .build()
            .create(LMS::class.java)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideNotificationId(): AtomicInteger {
        return AtomicInteger(0)
    }
}
