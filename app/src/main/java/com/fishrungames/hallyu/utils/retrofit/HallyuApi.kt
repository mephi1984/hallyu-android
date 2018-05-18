package com.fishrungames.hallyu.utils.retrofit

import com.fishrungames.hallyu.models.requests.CardTestQuestionRequest
import com.fishrungames.hallyu.models.requests.NumeralTestQuestionRequest
import com.fishrungames.hallyu.models.requests.TranslateTextRequest
import com.fishrungames.hallyu.models.responses.CardTestQuestionResponse
import com.fishrungames.hallyu.models.responses.NumeralTestQuestionResponse
import com.fishrungames.hallyu.models.responses.TranslateTextResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface HallyuApi {

    @POST("/number")
    fun getNumeralTestQuestion(@Body request: NumeralTestQuestionRequest): Call<NumeralTestQuestionResponse>

    @POST("/requestCard")
    fun getCardTestQuestion(@Body request: CardTestQuestionRequest): Call<CardTestQuestionResponse>

    @POST("/translate")
    fun translateKoreanText(@Body request: TranslateTextRequest): Call<TranslateTextResponse>

}