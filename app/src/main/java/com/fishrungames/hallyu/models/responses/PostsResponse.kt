package com.fishrungames.hallyu.models.responses

import com.fishrungames.hallyu.models.Post
import com.google.gson.annotations.SerializedName

class PostsResponse {

    var result: Boolean? = null

    @SerializedName("response")
    var posts: List<Post>? = null

    var message: String? = null

    fun haveMessage(): Boolean {
        return message != null && !message!!.isEmpty()
    }

}