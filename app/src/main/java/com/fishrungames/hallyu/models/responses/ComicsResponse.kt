package com.fishrungames.hallyu.models.responses

import com.fishrungames.hallyu.models.Comics
import com.google.gson.annotations.SerializedName

class ComicsResponse {

    var result: Boolean? = null

    @SerializedName("response")
    var comics: List<Comics>? = null

    var message: String? = null

    fun haveMessage(): Boolean {
        return message != null && !message!!.isEmpty()
    }

}