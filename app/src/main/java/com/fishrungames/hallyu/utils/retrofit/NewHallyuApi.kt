package com.fishrungames.hallyu.utils.retrofit

import com.fishrungames.hallyu.models.responses.ComicsResponse
import retrofit2.Call
import retrofit2.http.GET

interface NewHallyuApi {

    @GET("/comics/getList")
    fun getComics(): Call<ComicsResponse>

}