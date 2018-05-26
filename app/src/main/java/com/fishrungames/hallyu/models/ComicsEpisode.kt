package com.fishrungames.hallyu.models

import java.io.Serializable

class ComicsEpisode : Serializable {

    var id: Int? = null

    var seriesId: Int? = null

    var title: String? = null

    var description: String? = null

    var position: Int? = null

    var picturesCount: Int? = null

    var images: List<EpisodePicture>? = null

}