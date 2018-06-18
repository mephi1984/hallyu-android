package com.fishrungames.hallyu.models.dictionary

class CompoundVerb {

    var mainWordStruct: TranslatedWord? = null

    var secondaryWordStruct: TranslatedWord? = null

    var verbose: String? = null

    var originalPhrase: String? = null

    var lessons: List<Lesson>? = null

    var complexVerbType: String? = null

    var selectedBlock: Int? = 0

}