package com.fishrungames.hallyu.models.responses

import com.fishrungames.hallyu.models.OnRequestWordTranslation
import com.google.gson.annotations.SerializedName

class TranslateTextResponse {

    @SerializedName("OnRequestWordTranslation")
    var translatedText: OnRequestWordTranslation? = null

    fun haveTranslatedText(): Boolean {
        return translatedText != null
    }

}