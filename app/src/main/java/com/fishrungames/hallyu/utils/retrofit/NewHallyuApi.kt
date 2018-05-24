package com.fishrungames.hallyu.utils.retrofit

import com.fishrungames.hallyu.models.responses.ComicsEpisodesResponse
import com.fishrungames.hallyu.models.responses.ComicsResponse
import com.fishrungames.hallyu.models.responses.EpisodePicturesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewHallyuApi {

    @GET("/comics/getList")
    fun getComics(): Call<ComicsResponse>

    @GET("/comics/getEpisodesList")
    fun getEpisodes(@Query("id") comicsId: String): Call<ComicsEpisodesResponse>

    @GET("/comics/getPicturesByEpisode")
    fun getPictures(@Query("id") episodeId: String): Call<EpisodePicturesResponse>

}