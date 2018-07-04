package com.fishrungames.hallyu.models.responses

import com.fishrungames.hallyu.models.dictionary.Lesson
import com.google.gson.annotations.SerializedName

class LessonResponse {

    var result: Boolean? = null

    @SerializedName("response")
    var lessons: List<Lesson>? = null

    var message: String? = null

    fun haveMessage(): Boolean {
        return message != null && !message!!.isEmpty()
    }

}