package com.luzarelymtz.schoolsfinder.PlaceApi

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import com.luzarelymtz.schoolsfinder.BuildConfig
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MapClient {

    companion object {
        const val BASE_URL =
            "https://maps.googleapis.com/maps/api/place/textsearch/"

        fun provideApiService(): MapsApiService {
            return provideRetrofit(BASE_URL, getClient())
                .create(MapsApiService::class.java)
        }

        private fun provideRetrofit(baseUrl: String, client: OkHttpClient): Retrofit {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }

        private fun getClient(): OkHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = when (BuildConfig.BUILD_TYPE) {
                "debug" -> HttpLoggingInterceptor.Level.BODY
                else -> HttpLoggingInterceptor.Level.NONE
            }

            return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
//                .addInterceptor { chain ->
//                    var request = chain.request()
//                    val url = request.url()
//                        .newBuilder()
//                        .addQueryParameter("key", API_KEY)
//                        .build()
//                    request = request.newBuilder()
//                        .url(url)
//                        .build()
//                    chain.proceed(request)
//                }
                .build()
        }
    }
}
