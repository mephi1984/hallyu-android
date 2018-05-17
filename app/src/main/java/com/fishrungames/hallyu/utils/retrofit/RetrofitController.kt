package com.fishrungames.hallyu.utils.retrofit

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitController {

    private val SERVER_URL = "http://hallyu.fishrungames.com/"

    private var hallyuApi: HallyuApi? = null

    private fun getLogging(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return logging
    }

    private fun getOkHttp(): OkHttpClient {
        return OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(getLogging())
                .build()
    }

    fun getHallyuApiApi(): HallyuApi {
        if (hallyuApi == null) {
            val gson = GsonBuilder().setLenient().create()
            val retrofit = Retrofit.Builder()
                    .baseUrl(SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(getOkHttp())
                    .build()

            hallyuApi = retrofit.create(HallyuApi::class.java)
        }
        return hallyuApi!!
    }

}