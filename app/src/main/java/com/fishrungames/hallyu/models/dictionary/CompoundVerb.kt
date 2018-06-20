package com.fishrungames.hallyu.models.dictionary

import com.google.gson.annotations.SerializedName

class CompoundVerb {

    @SerializedName("secondaryWordStruct")
    var mainWordStruct: TranslatedWord? = null

    @SerializedName("mainWordStruct")
    var secondaryWordStruct: TranslatedWord? = null

    var verbose: String? = null

    var originalPhrase: String? = null

    var lessons: List<Lesson>? = null

    var complexVerbType: String? = null

    var selectedBlock: Int? = 0

}