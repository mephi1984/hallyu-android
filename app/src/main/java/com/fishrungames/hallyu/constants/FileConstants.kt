package com.fishrungames.hallyu.constants

object FileConstants {

    private val FILE_EPISODE_PICTURES_DATA: String = "fileEpisodePictures"

    fun getEpisodeFilename(episodeId: String): String {
        return FILE_EPISODE_PICTURES_DATA + episodeId
    }

}