package com.fishrungames.hallyu.utils.retrofit

import com.fishrungames.hallyu.models.responses.*
import retrofit2.Call
import retrofit2.http.*

interface NewHallyuApi {

    @POST("/user/login")
    @FormUrlEncoded
    fun vkLogin(@Field ("accessToken") accessToken: String): Call<VKAuthorizationResponse>

    @GET("/comics/getList")
    fun getComics(): Call<ComicsResponse>

    @GET("/comics/getEpisodesList")
    fun getEpisodes(@Query("id") comicsId: String): Call<ComicsEpisodesResponse>

    @GET("/news/getPosts")
    fun getPosts(@Query("id") id: String): Call<PostsResponse>

    @GET("/news/getComments")
    fun getComments(@Query ("id") postId: String): Call<PostCommentsResponse>

    @GET("/news/getList")
    fun getPostCategories(): Call<PostCategoriesResponse>

}