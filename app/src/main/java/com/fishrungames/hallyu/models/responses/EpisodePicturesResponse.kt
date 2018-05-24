package com.fishrungames.hallyu.models.responses

import com.fishrungames.hallyu.models.EpisodePicture
import com.google.gson.annotations.SerializedName

class EpisodePicturesResponse {

    var result: Boolean? = null

    @SerializedName("response")
    var pictures: List<EpisodePicture>? = null

    var message: String? = null

    fun haveMessage(): Boolean {
        return message != null && !message!!.isEmpty()
    }

}