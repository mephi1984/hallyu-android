package com.fishrungames.hallyu.models.responses

import com.fishrungames.hallyu.models.PostCategory
import com.google.gson.annotations.SerializedName

class PostCategoriesResponse {

    var result: Boolean? = null

    @SerializedName("response")
    var categories: List<PostCategory>? = null

    var message: String? = null

    fun haveMessage(): Boolean {
        return message != null && !message!!.isEmpty()
    }

}