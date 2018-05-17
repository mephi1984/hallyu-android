package com.fishrungames.hallyu.models.responses

import com.fishrungames.hallyu.models.OnRequestChineseNumberRecognize
import com.google.gson.annotations.SerializedName

class NumeralTestQuestionResponse {

    @SerializedName("OnRequestChineseNumberRecognize")
    var question: OnRequestChineseNumberRecognize? = null

    fun haveQuestion(): Boolean {
        return question != null
    }

}