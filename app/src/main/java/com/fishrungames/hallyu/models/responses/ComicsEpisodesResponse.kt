package com.fishrungames.hallyu.models.responses

import com.fishrungames.hallyu.models.ComicsEpisode
import com.google.gson.annotations.SerializedName

class ComicsEpisodesResponse {

    var result: Boolean? = null

    @SerializedName("response")
    var episodes: List<ComicsEpisode>? = null

    var message: String? = null

    fun haveMessage(): Boolean {
        return message != null && !message!!.isEmpty()
    }

}