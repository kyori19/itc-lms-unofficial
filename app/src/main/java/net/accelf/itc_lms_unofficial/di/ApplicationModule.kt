package net.accelf.itc_lms_unofficial.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import net.accelf.itc_lms_unofficial.BuildConfig
import net.accelf.itc_lms_unofficial.network.DocumentConverterFactory
import net.accelf.itc_lms_unofficial.network.LMS
import net.accelf.itc_lms_unofficial.network.lmsHostUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun provideLmsClient(cookieJar: SavedCookieJar): LMS {
        val okHttpClient = OkHttpClient.Builder().apply {
            addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE)
            )
            cookieJar(cookieJar)
        }.build()

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(lmsHostUrl)
            .addConverterFactory(DocumentConverterFactory())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .build()
            .create(LMS::class.java)
    }
}
