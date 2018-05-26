package com.fishrungames.hallyu.constants

object FileConstants {

    private val FILE_COMICS_EPISODES_DATA: String = "fileEpisodesComicsData"

    val FILE_COMICS_DATA: String = "fileComicsData"

    fun getComicsEpisodesFilename(comicsId: String): String {
        return FILE_COMICS_EPISODES_DATA + comicsId
    }

}