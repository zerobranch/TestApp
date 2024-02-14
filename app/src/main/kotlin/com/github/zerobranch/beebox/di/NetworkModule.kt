package com.github.zerobranch.beebox.di

import com.github.zerobranch.beebox.BuildConfig
import com.github.zerobranch.beebox.commons_android.utils.ext.trustAllCertificate
import com.github.zerobranch.beebox.data.source.local.AppConstants
import com.github.zerobranch.beebox.data.source.remote.Api
import com.github.zerobranch.beebox.data.source.remote.DeviceInterceptor
import com.github.zerobranch.beebox.logging.debug
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import zerobranch.androidremotedebugger.logging.NetLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        deviceInterceptor: DeviceInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .trustAllCertificate
        .addInterceptor(loggingInterceptor)
        .addInterceptor(deviceInterceptor)
        .addInterceptor(NetLoggingInterceptor())
        .retryOnConnectionFailure(true)
        .connectTimeout(AppConstants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(AppConstants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(AppConstants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor { msg -> javaClass.debug("OkHttp", msg) }
            .apply {
                level =
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
            }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient,
        moshi: Moshi
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(client)
        .build()

    @Singleton
    @Provides
    fun provideApi(retrofit: Retrofit): Api = retrofit.create()
}