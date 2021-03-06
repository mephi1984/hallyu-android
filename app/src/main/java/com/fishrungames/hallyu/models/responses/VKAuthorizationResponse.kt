package com.fishrungames.hallyu.models.responses

import com.fishrungames.hallyu.models.User
import com.google.gson.annotations.SerializedName

class VKAuthorizationResponse {

    var result: Boolean? = null

    @SerializedName("response")
    var user: User? = null

    var message: String? = null

    fun haveMessage(): Boolean {
        return message != null && !message!!.isEmpty()
    }

}