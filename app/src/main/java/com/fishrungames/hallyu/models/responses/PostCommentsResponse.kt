package com.fishrungames.hallyu.models.responses

import com.fishrungames.hallyu.models.PostComment
import com.google.gson.annotations.SerializedName

class PostCommentsResponse {

    var result: Boolean? = null

    @SerializedName("response")
    var comments: List<PostComment>? = null

    var message: String? = null

    fun haveMessage(): Boolean {
        return message != null && !message!!.isEmpty()
    }

}