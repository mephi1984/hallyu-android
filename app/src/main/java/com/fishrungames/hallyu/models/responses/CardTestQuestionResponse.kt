package com.fishrungames.hallyu.models.responses

import com.fishrungames.hallyu.models.OnRequestCard
import com.google.gson.annotations.SerializedName

class CardTestQuestionResponse {

    @SerializedName("OnRequestCard")
    var question: OnRequestCard? = null

    fun haveQuestion(): Boolean {
        return question != null
    }

}